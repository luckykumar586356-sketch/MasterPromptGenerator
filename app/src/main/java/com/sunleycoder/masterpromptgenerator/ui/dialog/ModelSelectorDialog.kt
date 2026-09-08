package com.sunleycoder.masterpromptgenerator.ui.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.sunleycoder.masterpromptgenerator.R
import com.sunleycoder.masterpromptgenerator.data.model.AiProvider
import com.sunleycoder.masterpromptgenerator.data.model.ModelInfo
import com.sunleycoder.masterpromptgenerator.generator.AiApiService
import com.sunleycoder.masterpromptgenerator.ui.adapter.ModelSelectorAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModelSelectorDialog(
    context: Context,
    private val provider: AiProvider,
    private val apiKey: String,
    private val currentSelectedId: String,
    private val onModelChosen: (ModelInfo) -> Unit
) : Dialog(context) {

    private val apiService = AiApiService()
    private lateinit var adapter: ModelSelectorAdapter
    private var allModels: List<ModelInfo> = emptyList()
    private var filterFreeOnly: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_model_selector)

        window?.setLayout(
            (context.resources.displayMetrics.widthPixels * 0.94).toInt(),
            android.view.ViewGroup.LayoutParams.WRAP_CONTENT
        )

        val tvProviderTitle = findViewById<TextView>(R.id.tvModelProviderTitle)
        val rvModels = findViewById<RecyclerView>(R.id.rvModelsList)
        val pbFetching = findViewById<ProgressBar>(R.id.pbFetchingModels)
        val btnRefresh = findViewById<ImageView>(R.id.btnRefreshModels)
        val btnClose = findViewById<MaterialButton>(R.id.btnCloseModelSelector)
        val tabAll = findViewById<TextView>(R.id.tabFilterAllModels)
        val tabFree = findViewById<TextView>(R.id.tabFilterFreeOnly)

        tvProviderTitle.text = "${provider.displayName} • Available Models"

        allModels = ModelInfo.getCuratedModels(provider)

        adapter = ModelSelectorAdapter(allModels, currentSelectedId) { model ->
            onModelChosen(model)
            dismiss()
        }

        rvModels.layoutManager = LinearLayoutManager(context)
        rvModels.adapter = adapter

        tabAll.setOnClickListener {
            filterFreeOnly = false
            tabAll.setBackgroundResource(R.drawable.bg_chip_selected)
            tabAll.setTextColor(ContextCompat.getColor(context, R.color.chip_selected_text))
            tabFree.setBackgroundResource(R.drawable.bg_chip_unselected)
            tabFree.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            applyFilter()
        }

        tabFree.setOnClickListener {
            filterFreeOnly = true
            tabFree.setBackgroundResource(R.drawable.bg_chip_selected)
            tabFree.setTextColor(ContextCompat.getColor(context, R.color.chip_selected_text))
            tabAll.setBackgroundResource(R.drawable.bg_chip_unselected)
            tabAll.setTextColor(ContextCompat.getColor(context, R.color.text_primary))
            applyFilter()
        }

        btnClose.setOnClickListener { dismiss() }

        btnRefresh.setOnClickListener {
            fetchLive(pbFetching)
        }

        // If key is present and not offline, fetch live models in background
        if (apiKey.isNotBlank() && provider != AiProvider.OFFLINE_MASTER) {
            fetchLive(pbFetching)
        }
    }

    private fun fetchLive(pbFetching: ProgressBar) {
        pbFetching.visibility = View.VISIBLE
        CoroutineScope(Dispatchers.IO).launch {
            val result = apiService.fetchLiveModels(provider, apiKey)
            withContext(Dispatchers.Main) {
                pbFetching.visibility = View.GONE
                if (result.isSuccess) {
                    val fetched = result.getOrNull()
                    if (!fetched.isNullOrEmpty()) {
                        allModels = fetched
                        applyFilter()
                    }
                }
            }
        }
    }

    private fun applyFilter() {
        val filtered = if (filterFreeOnly) {
            allModels.filter { it.isFree }
        } else {
            allModels
        }
        adapter.updateModels(filtered)
    }
}
