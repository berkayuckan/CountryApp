package com.berkayuckan.countryapp.view


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.berkayuckan.countryapp.R
import com.berkayuckan.countryapp.adapter.CountryAdapter
import com.berkayuckan.countryapp.databinding.FragmentFeedBinding
import com.berkayuckan.countryapp.model.Country
import com.berkayuckan.countryapp.viewmodel.FeedViewModel

class FeedFragment : Fragment() {

    private lateinit var dataBinding : FragmentFeedBinding

    private lateinit var viewModel : FeedViewModel
    private val countryAdapter = CountryAdapter(arrayListOf())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_feed,container,false)
        // Inflate the layout for this fragment
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(FeedViewModel::class.java)
        viewModel.refreshData()

        dataBinding.countryList.layoutManager = LinearLayoutManager(context)
        dataBinding.countryList.adapter = countryAdapter

        dataBinding.swipeRefreshLayout.setOnRefreshListener {
            dataBinding.countryList.visibility = View.GONE
            dataBinding.countryError.visibility = View.GONE
            dataBinding.countryLoading.visibility = View.VISIBLE
            viewModel.refreshFromAPI()
            dataBinding.swipeRefreshLayout.isRefreshing = false
        }
        observeLiveData()

    }

    private fun observeLiveData() {
        viewModel.countries.observe(viewLifecycleOwner, Observer {countries ->

            countries?.let {
                dataBinding.countryList.visibility = View.VISIBLE
                countryAdapter.updateCountryList(countries)
            }

        })

        viewModel.countryError.observe(viewLifecycleOwner, Observer { error->
            error?.let {
                if(it) {
                    dataBinding.countryError.visibility = View.VISIBLE
                } else {
                    dataBinding.countryError.visibility = View.GONE
                }
            }
        })

        viewModel.countryLoading.observe(viewLifecycleOwner, Observer { loading->
            loading?.let {
                if (it) {
                    dataBinding.countryLoading.visibility = View.VISIBLE
                    dataBinding.countryList.visibility = View.GONE
                    dataBinding.countryError.visibility = View.GONE
                } else {
                    dataBinding.countryLoading.visibility = View.GONE
                }
            }
        })
    }


}