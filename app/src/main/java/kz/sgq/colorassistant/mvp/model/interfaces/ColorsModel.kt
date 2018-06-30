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

import kz.sgq.colorassistant.infraestructure.networking.gson.ColorsGson
import kz.sgq.colorassistant.room.table.Colors
import kz.sgq.colorassistant.ui.util.ItemColor

interface ColorsModel {
    fun initRandom(size: Int)
    fun getNumbers(): IntArray
    fun getCheck(): Int
    fun getIdList(): MutableList<Int>
    fun getLikeList(): MutableList<Boolean>
    fun setLike(index: Int, like: Boolean)
    fun getItemColor(colors: MutableList<Colors>): MutableList<ItemColor>
    fun isLoading(): Boolean
    fun setLoading(loading: Boolean)
    fun setRandomSize(size: Int)
    fun clear()
    fun getVisibleThreshold(): Int
    fun converterToItemColor(list: MutableList<ColorsGson>): MutableList<ItemColor>
}