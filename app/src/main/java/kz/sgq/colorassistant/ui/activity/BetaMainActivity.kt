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

package kz.sgq.colorassistant.ui.activity

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v4.app.ActivityCompat
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.arellomobile.mvp.MvpAppCompatActivity
import com.arellomobile.mvp.presenter.InjectPresenter
import kotlinx.android.synthetic.main.activity_beta_main.*
import kz.sgq.colorassistant.R
import kz.sgq.colorassistant.mvp.model.MainModelImpl
import kz.sgq.colorassistant.mvp.presenter.MainPresenter
import kz.sgq.colorassistant.mvp.view.MainView
import kz.sgq.colorassistant.room.table.Cloud
import kz.sgq.colorassistant.ui.fragment.dialog.QRScanDialog
import kz.sgq.colorassistant.ui.fragment.sheet.MenuBottomSheet
import kz.sgq.colorassistant.ui.util.CodeActivity
import kz.sgq.colorassistant.ui.view.ItemColor
import kotlin.math.PI

class BetaMainActivity : MvpAppCompatActivity(), MainView {
    @InjectPresenter
    lateinit var presenter: MainPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_beta_main)
        initActionBar()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean = when (item?.itemId) {
        android.R.id.home -> {

            presenter.openMenu()

            true
        }
        R.id.settings -> {

            startActivity(Intent(this, SettingsActivity::class.java))

            true
        }
        R.id.qr -> {

            checkCameraPermission()

            true
        }
        R.id.image_scan -> {

            startActivityForResult(
                    Intent(this, ImageActivity::class.java),
                    CodeActivity.IMAGE_SCAN.ID
            )

            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun global() {

        presenter.setCurrentFragment(MainModelImpl.MainFragment.GLOBAL)
        fab.setImageDrawable(resources.getDrawable(R.drawable.like))
    }

    override fun like() {

        presenter.setCurrentFragment(MainModelImpl.MainFragment.LIKE)
        fab.setImageDrawable(resources.getDrawable(R.drawable.cancel))
    }

    override fun cloud() {

        presenter.setCurrentFragment(MainModelImpl.MainFragment.CLOUD)
        fab.setImageDrawable(resources.getDrawable(R.drawable.constructor))
    }

    override fun constructor() {

        startActivityForResult(
                Intent(this, ConstructorActivity::class.java),
                CodeActivity.CONSTRUCTOR.ID
        )
    }

    override fun cancel() {

        presenter.setCurrentFragment(MainModelImpl.MainFragment.LIKE)
        fab.setImageDrawable(resources.getDrawable(R.drawable.like))
    }

    override fun openMenu(fragmentCurrent: MainModelImpl.MainFragment) {

        MenuBottomSheet().apply {

            setFragmentCurrent(fragmentCurrent)
            setClick(object : MenuBottomSheet.OnClickListener {
                override fun onClick(fragmentCurrent: MainModelImpl.MainFragment) {

                    presenter.setCurrentFragment(fragmentCurrent)
                    presenter.menuClick()
                }
            })
            show(supportFragmentManager, "menu_bottom_sheet")
        }
    }

    override fun answerQR(cloud: Cloud) {

        QRScanDialog().apply {

            cloud(cloud)
            clickListener(initClickAnswer(cloud))
            show(supportFragmentManager, "qr_dialog")
        }
    }

    override fun errorQR() {

        Snackbar.make(
                coordinator_layout,
                resources.getString(R.string.snack_qr_scan_error_title),
                Snackbar.LENGTH_LONG
        ).apply {

            setAction(
                    resources.getString(R.string.snack_qr_scan_error_click),
                    initClickError()
            )
            setActionTextColor(resources.getColor(R.color.snack_error))
            view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
                    .setTextColor(resources.getColor(R.color.snack_text))

            val params = view.layoutParams as CoordinatorLayout.LayoutParams

            params.setMargins(
                    params.leftMargin,
                    params.topMargin,
                    params.rightMargin,
                    params.bottomMargin + bar.height + (fab.height / (PI / 2)).toInt()
            )

            view.layoutParams = params

            show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK)
            presenter.initResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == 10) {

            if ((permissions[0] == Manifest.permission.CAMERA) &&
                    (grantResults[0] == PackageManager.PERMISSION_GRANTED))
                openScanActivity()
        } else
            super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun initClickAnswer(
            cloud: Cloud
    ): ItemColor.OnClickListener = object : ItemColor.OnClickListener {
        override fun onClick() {

            presenter.save(cloud)
        }
    }

    private fun initClickError(): View.OnClickListener = View.OnClickListener {

        checkCameraPermission()
    }

    private fun checkCameraPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(
                    parent,
                    arrayOf(Manifest.permission.CAMERA),
                    10
            )
        else
            openScanActivity()
    }

    private fun openScanActivity() {
        val intent = Intent(this, QRCodeScanActivity::class.java)

        startActivityForResult(intent, CodeActivity.QR_SCAN.ID)
    }

    private fun initActionBar() {

        setSupportActionBar(bar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.menu)
        }
        fab.setOnClickListener {

            presenter.fabClick()
        }
    }
}