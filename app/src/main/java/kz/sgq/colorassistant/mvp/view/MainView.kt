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

package kz.sgq.colorassistant.mvp.view

import com.arellomobile.mvp.MvpView
import kz.sgq.colorassistant.mvp.model.MainModelImpl
import kz.sgq.colorassistant.room.table.Cloud

interface MainView : MvpView {

    fun answerQR(cloud: Cloud)

    fun errorQR()

    fun global()

    fun like()

    fun cloud()

    fun constructor()

    fun cancel()

    fun openMenu(fragmentCurrent: MainModelImpl.MainFragment)
}