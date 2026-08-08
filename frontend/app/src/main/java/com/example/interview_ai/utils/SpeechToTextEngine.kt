package com.example.interview_ai.utils

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer

class SpeechToTextEngine(context: Context) {

    private var speechRecognizer: SpeechRecognizer? = null
    private val recognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
        putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        )

        putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US")

        putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)

        // Keep listening longer
        putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS,
            3000L
        )

        putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS,
            3000L
        )

        putExtra(
            RecognizerIntent.EXTRA_SPEECH_INPUT_MINIMUM_LENGTH_MILLIS,
            60000L
        )

        putExtra(
            RecognizerIntent.EXTRA_MAX_RESULTS,
            1
        )
    }

    init {
        if (SpeechRecognizer.isRecognitionAvailable(context)) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context)
        }
    }



    fun startListening(
        onVoiceActivity: () -> Unit,
        onResults: (String) -> Unit,
        onPartialResults: (String) -> Unit,
        onError: (Int) -> Unit
    ){
        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(rmsdB: Float) {

                if (rmsdB > -2f) {
                    onVoiceActivity()
                }

            }
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                onError(error)
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onResults(matches[0])
                }
            }

            override fun onPartialResults(partialResults: Bundle?) {
                val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                if (!matches.isNullOrEmpty()) {
                    onPartialResults(matches[0])
                }
            }

            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(recognizerIntent)
    }

    fun stopListening() {
        speechRecognizer?.stopListening()
    }

    fun destroy() {
        speechRecognizer?.destroy()
        speechRecognizer = null
    }
}
