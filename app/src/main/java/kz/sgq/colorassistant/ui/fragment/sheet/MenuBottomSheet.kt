/*
 * Copyright 2018 Vlad Weber-Pflaumer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package kz.sgq.colorassistant.ui.fragment.sheet

import android.annotation.SuppressLint
import android.app.Dialog
import android.support.design.widget.BottomSheetBehavior
import android.support.design.widget.BottomSheetDialogFragment
import android.view.View
import kotlinx.android.synthetic.main.bottom_sheet_menu.view.*
import kz.sgq.colorassistant.R
import kz.sgq.colorassistant.mvp.model.MainModelImpl
import kz.sgq.colorassistant.mvp.model.MainModelImpl.MainFragment.*

class MenuBottomSheet : BottomSheetDialogFragment() {
    private lateinit var fragmentCurrent: MainModelImpl.MainFragment
    private lateinit var clickListener: OnClickListener

    interface OnClickListener {

        fun onClick(fragmentCurrent: MainModelImpl.MainFragment)
    }

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)

        val view = View.inflate(context, R.layout.bottom_sheet_menu, null)

        dialog!!.setContentView(view)

        val bottomSheet = BottomSheetBehavior.from(view.parent as View)

        if (bottomSheet != null) {

            setColor()
            view.global.setOnClickListener(initClickListener(GLOBAL))
            view.cloud.setOnClickListener(initClickListener(CLOUD))
            bottomSheet.setBottomSheetCallback(initCallback())
            view.requestLayout()
        }
    }

    fun setFragmentCurrent(fragmentCurrent: MainModelImpl.MainFragment) {
        this.fragmentCurrent = fragmentCurrent
    }

    fun setClick(clickListener: OnClickListener) {
        this.clickListener = clickListener
    }

    private fun setColor() {

    }

    private fun initClickListener(
            fragmentCurrent: MainModelImpl.MainFragment
    ): View.OnClickListener = View.OnClickListener {

        when (fragmentCurrent) {
            GLOBAL -> {

                clickListener.onClick(CLOUD)

                dismiss()
            }
            CLOUD -> {

                clickListener.onClick(GLOBAL)

                dismiss()
            }
            else -> {

                clickListener.onClick(CLOUD)
                dismiss()
            }
        }
    }

    private fun initCallback(): BottomSheetBehavior.BottomSheetCallback =
            object : BottomSheetBehavior.BottomSheetCallback() {
                override fun onSlide(p0: View, p1: Float) {

                }

                override fun onStateChanged(p0: View, p1: Int) {

                    if (p1 == BottomSheetBehavior.STATE_HIDDEN)
                        dismiss()
                }
            }
}