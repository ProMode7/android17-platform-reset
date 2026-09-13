package com.promode.npu.ui

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.promode.npu.inference.ClassificationOutcome
import com.promode.npu.inference.ImageClassifier
import com.promode.npu.inference.NpuFeature
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

sealed interface ClassifierUiState {
    data object Idle : ClassifierUiState
    data object Running : ClassifierUiState
    data class Done(val outcome: ClassificationOutcome) : ClassifierUiState
    data class Failed(val message: String) : ClassifierUiState
}

class ClassifierViewModel(application: Application) : AndroidViewModel(application) {

    private val classifier = ImageClassifier(application)

    val npuDeclaredByPlatform: Boolean = NpuFeature.isDeclaredByPlatform(application)

    private val _uiState = MutableStateFlow<ClassifierUiState>(ClassifierUiState.Idle)
    val uiState: StateFlow<ClassifierUiState> = _uiState.asStateFlow()

    fun classify(bitmap: Bitmap) {
        _uiState.value = ClassifierUiState.Running
        viewModelScope.launch {
            val result = runCatching {
                withContext(Dispatchers.Default) { classifier.classify(bitmap) }
            }
            _uiState.value = result.fold(
                onSuccess = { ClassifierUiState.Done(it) },
                onFailure = { ClassifierUiState.Failed(it.message ?: it.javaClass.simpleName) },
            )
        }
    }

    override fun onCleared() {
        classifier.close()
    }
}
