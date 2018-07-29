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

package kz.sgq.colorassistant.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_color_list.*
import kz.sgq.colorassistant.R
import kz.sgq.colorassistant.mvp.presenter.LikesPresenter
import kz.sgq.colorassistant.mvp.view.LikesView
import kz.sgq.colorassistant.ui.activity.ComboActivity
import kz.sgq.colorassistant.ui.adapters.RecyclerColorsAdapter
import kz.sgq.colorassistant.ui.util.ItemColor
import kz.sgq.colorassistant.ui.util.interfaces.OnItemColorClickListener
import kz.sgq.colorassistant.ui.util.interfaces.OnSelectedButtonListener
import java.io.Serializable

class LikesFragment : MvpAppCompatFragment(), LikesView {
    @InjectPresenter
    lateinit var presenter: LikesPresenter
    private lateinit var listener: OnSelectedButtonListener
    private var adapter = RecyclerColorsAdapter()
    private lateinit var layoutManager: LinearLayoutManager

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_color_list, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listener = (parentFragment as OnSelectedButtonListener)
        layoutManager = LinearLayoutManager(view.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_colors.layoutManager = layoutManager
        rv_colors.adapter = adapter
        onClickListenerAdapter()
        setUpLoadMoreListener()
    }

    override fun clearItemsDB() {
        adapter.clearItems()
    }

    override fun showLoadDB() {
        textNoItem.visibility = View.VISIBLE
        rv_colors.visibility = View.GONE
    }

    override fun showColorList() {
        textNoItem.visibility = View.GONE
        rv_colors.visibility = View.VISIBLE
    }

    override fun addItemsDB(item: ItemColor) {
        adapter.addItem(item)
    }

    override fun showActivityInfo(list: MutableList<String>) {
        val intent = Intent(context, ComboActivity::class.java)
        intent.putExtra("map", list as Serializable)
        startActivity(intent)
    }

    override fun deleteItem(id: Int) {
        adapter.deleteItem(id)
    }

    private fun onClickListenerAdapter() {
        adapter.setOnItemClickListener(object : OnItemColorClickListener {
            override fun likeClick(view: View, id: Int, like: Boolean) {
                listener.onLikeClickListener(id)
                presenter.onItemLikeClick(view, id, like)
            }

            override fun viewClick(view: View, itemColor: ItemColor) {
                presenter.onItemViewClick(view, itemColor)
            }
        })
    }

    private fun setUpLoadMoreListener() {
        rv_colors.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                presenter.handlerColorListener(layoutManager.itemCount,
                        layoutManager.findLastVisibleItemPosition())
            }
        })
    }
}