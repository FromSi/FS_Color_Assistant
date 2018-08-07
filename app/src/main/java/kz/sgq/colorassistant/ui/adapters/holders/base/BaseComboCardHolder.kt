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

package kz.sgq.colorassistant.ui.adapters.holders.base

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import kotlinx.android.synthetic.main.item_colors.view.*
import kz.sgq.colorassistant.ui.util.ItemColor

open class BaseComboCardHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {

    fun setImagesView(itemColor: ItemColor) {

        for (i in 0..4) {

            itemView.itemsTwo.visibility = View.VISIBLE

            if (itemColor.colors.size > i) {

                listImage[i]?.setBackgroundColor(Color
                        .parseColor(itemColor.colors[i]))
                listImage[i]?.visibility = View.VISIBLE
            } else
                listImage[i]?.visibility = View.GONE
        }
    }

    fun onLoadVisibly(visibly: Int) {
        itemView.itemsTwo.visibility = visibly
    }

    fun onLoadVisibly(b: Boolean) {
        if (b)
            itemView.itemsTwo.visibility = View.VISIBLE
        else
            itemView.itemsTwo.visibility = View.GONE
    }

    private val listImage: Array<ImageView?> = arrayOf(
            itemView?.itemOne,
            itemView?.itemTwo,
            itemView?.itemThree,
            itemView?.itemFour,
            itemView?.itemFive
    )
}