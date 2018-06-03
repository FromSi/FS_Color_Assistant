package kz.sgq.colorassistant.ui.fragment

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
import kz.sgq.colorassistant.mvp.presenter.ColorsPresenter
import kz.sgq.colorassistant.mvp.view.ColorsView
import kz.sgq.colorassistant.ui.adapters.RecyclerColorsAdapter
import kz.sgq.colorassistant.ui.util.ItemColor
import kz.sgq.colorassistant.ui.util.interfaces.OnItemClickListener

class ColorsFragment : MvpAppCompatFragment(), ColorsView {
    @InjectPresenter
    lateinit var presenter: ColorsPresenter

    private var adapter = RecyclerColorsAdapter()
    private lateinit var layoutManager: LinearLayoutManager

//    private var mPager: Int = 0
//
//    companion object {
//        fun newInstance(page: Int): ColorsFragment {
//            val args = Bundle()
//            args.putSerializable("ARG_PAGE", page)
//            val fragment = ColorsFragment()
//            fragment.arguments = args
//            return fragment
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        mPager = arguments.getInt("ARG_PAGE")
//    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View = inflater.inflate(R.layout.fragment_color_list, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutManager = LinearLayoutManager(view.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_colors.layoutManager = layoutManager
        rv_colors.adapter = adapter
        onClickListenerAdapter()
        setUpLoadMoreListener()
    }

    override fun showLoadDB() {
        progressDownload.visibility = View.VISIBLE
        rv_colors.visibility = View.GONE
    }

    override fun showColorList() {
        progressDownload.visibility = View.GONE
        rv_colors.visibility = View.VISIBLE
    }

    override fun addItemsDB(colorList: MutableList<ItemColor>) {
        adapter.addItems(colorList)
    }

    override fun clearItemsDB() {
        adapter.clearItems()
        presenter.clear()
    }

    override fun updateItemsDB(index: Int) {
        adapter.updateItems(index)
    }

    override fun showActivityInfo(list: MutableList<String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun onClickListenerAdapter(){
        adapter.SetOnItemClickListener(object : OnItemClickListener {
            override fun onItemLikeClick(view: View, id: Int, like: Boolean) {
                presenter.onItemLikeClick(view, id, like)
            }

            override fun onItemViewClick(view: View, itemColor: ItemColor) {
                presenter.onItemViewClick(view, itemColor)
            }
        })
    }

    private fun setUpLoadMoreListener(){
        rv_colors.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                presenter.handlerColorListener(layoutManager.itemCount,
                        layoutManager.findLastVisibleItemPosition())
            }
        })
    }
}