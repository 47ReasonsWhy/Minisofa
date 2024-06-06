package com.sofascoreacademy.minisofa.ui.home.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import com.sofascoreacademy.minisofa.data.model.Event

class EventFragment : Fragment() {
    companion object {
        fun newInstance(event: Event): EventFragment {
            return EventFragment().apply {
                arguments = Bundle().apply {
                    putSerializable("event", event)
                }
            }
        }
    }
}