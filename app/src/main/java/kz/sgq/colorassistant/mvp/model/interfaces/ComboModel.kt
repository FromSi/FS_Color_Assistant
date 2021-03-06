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

package kz.sgq.colorassistant.mvp.model.interfaces

import kz.sgq.colorassistant.ui.util.ItemDetails

interface ComboModel {

    fun getSize(): Int

    fun getShare(): String

    fun getColor(index: Int): Int

    fun getValue(index: Int): String

    fun getSaturation(index: Int): MutableList<ItemDetails>

    fun getLightness(index: Int): MutableList<ItemDetails>

    fun initColorList(list: MutableList<String>)
}