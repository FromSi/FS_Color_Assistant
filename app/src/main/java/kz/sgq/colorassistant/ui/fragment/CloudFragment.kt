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

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.*
import android.widget.LinearLayout
import com.arellomobile.mvp.MvpAppCompatFragment
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.fragment_cloud.*
import kz.sgq.colorassistant.R
import kz.sgq.colorassistant.mvp.presenter.CloudPresenter
import kz.sgq.colorassistant.mvp.view.CloudView
import kz.sgq.colorassistant.room.table.Cloud
import kz.sgq.colorassistant.ui.activity.ComboActivity
import kz.sgq.colorassistant.ui.adapters.RecyclerCloudAdapter
import kz.sgq.colorassistant.ui.fragment.dialog.DeleteDialog
import kz.sgq.colorassistant.ui.fragment.dialog.InfoDialog
import kz.sgq.colorassistant.ui.view.ItemColor
import kz.sgq.colorassistant.ui.fragment.dialog.SaveDialog
import kz.sgq.colorassistant.ui.fragment.dialog.ShareDialog
import kz.sgq.colorassistant.ui.util.interfaces.*
import java.io.Serializable

class CloudFragment : MvpAppCompatFragment(), CloudView {
    private var adapter = RecyclerCloudAdapter()

    @InjectPresenter
    lateinit var presenter: CloudPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_cloud, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        settingToolBar()
        initRecyclerAdapter(view.context)
        initColorPicker()
    }

    override fun initColorList(list: MutableList<Cloud>) {
        adapter.addList(list)
    }

    override fun addItem(cloud: Cloud) {
        adapter.addItem(cloud)
    }

    override fun deleteItem(index: Int) {
        adapter.deleteItem(index)
    }

    override fun shareItem(text: String) {
        val dialog = ShareDialog()
        dialog.setText(text)
        dialog.show(fragmentManager, "share_dialog")
    }

    override fun showActivityInfo(list: MutableList<String>) {
        val intent = Intent(context, ComboActivity::class.java)
        intent.putExtra("map", list as Serializable)
        startActivity(intent)
    }

    private fun initRecyclerAdapter(context: Context) {
        val layoutManager = LinearLayoutManager(context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        cloud.layoutManager = layoutManager
        cloud.adapter = adapter

        clickListener()

        presenter.initInitList()
    }

    private fun initColorPicker() {
        color_picker.addLightnessView(lightness)
        color_picker.addSaturationView(saturation)

        createNewItemColor()
        createNewItemColor()
        createNewItemColor()

        color_picker.setItemColor(item_list.getChildAt(0) as ItemColor)

        add.setOnClickListener {
            ItemColor.ItemColor.boolDelete = true

            if (item_list.childCount <= 3) {
                createNewItemColor()
            } else {
                createNewItemColor()
                add.visibility = View.GONE
            }
        }
    }

    private fun createNewItemColor() {
        val itemColor = ItemColor(context)

        itemColor.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        )

        item_list.addView(itemColor)

        itemColor.setDeleteIndex(item_list.childCount - 1)

        itemColor.setOnItemColorListener(object : OnItemColorListener {
            override fun info(color: Int) {
                val dialog = InfoDialog()
                dialog.setColor(color)
                dialog.show(fragmentManager, "info_dialog")
            }

            override fun delete(index: Int) {
                val dialog = DeleteDialog()

                dialog.clickListener(index, object : OnDeleteItemListener {
                    override fun delete(index: Int) {
                        item_list.removeViewAt(index)
                        add.visibility = View.VISIBLE

                        if (item_list.childCount <= 3)
                            ItemColor.ItemColor.boolDelete = false

                        for (i in index until item_list.childCount)
                            (item_list.getChildAt(i) as ItemColor).setDeleteIndex(i)
                    }
                })

                dialog.show(fragmentManager, "delete_dialog")
            }
        })

        itemColor.setOnClickItemColorListener(object : OnClickItemColorListener {
            override fun onClick() {
                color_picker.setItemColor(itemColor)
            }
        })
    }

    private fun clickListener() {
        adapter.setOnItemClickListener(object : OnItemCloudClickListener {

            override fun viewClick(cloud: Cloud) {
                presenter.onItemViewClick(cloud)
            }

            override fun shareClick(cloud: Cloud) {
                presenter.onItemShareClick(cloud)
            }

            override fun deleteClick(cloud: Cloud, index: Int) {
                presenter.onItemDeleteClick(cloud, index)
            }
        })
    }

    private fun getColorHex(index: Int): String = (item_list.getChildAt(index) as ItemColor).getColorHex()

    private fun settingToolBar() {
        toolBar.title = getString(R.string.constructor)
        toolBar.inflateMenu(R.menu.constructor_menu)

        toolBar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.constructor -> {
                    toolBar.menu.findItem(R.id.constructor).isVisible = false
                    toolBar.menu.findItem(R.id.cloud).isVisible = true
                    toolBar.menu.findItem(R.id.save).isVisible = true

                    constructor.visibility = View.VISIBLE
                    cloud.visibility = View.GONE
                }

                R.id.cloud -> {
                    toolBar.menu.findItem(R.id.constructor).isVisible = true
                    toolBar.menu.findItem(R.id.cloud).isVisible = false
                    toolBar.menu.findItem(R.id.save).isVisible = false

                    constructor.visibility = View.GONE
                    cloud.visibility = View.VISIBLE
                }

                R.id.save -> {
                    val dialog = SaveDialog()

                    dialog.clickListener(object : OnClickListener {
                        override fun onClick() {
                            val cloud = when (item_list.childCount) {
                                3 -> Cloud(
                                        getColorHex(0),
                                        getColorHex(1),
                                        getColorHex(2)
                                )

                                4 -> Cloud(
                                        getColorHex(0),
                                        getColorHex(1),
                                        getColorHex(2),
                                        getColorHex(3)
                                )

                                5 -> Cloud(
                                        getColorHex(0),
                                        getColorHex(1),
                                        getColorHex(2),
                                        getColorHex(3),
                                        getColorHex(4)
                                )

                                else -> return
                            }

                            presenter.addItem(cloud)
                        }
                    })

                    dialog.show(fragmentManager, "save_dialog")
                }

                R.id.qr -> {

                }
            }
            false
        }
    }
}