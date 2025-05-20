package com.example.mybookapplication.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.VolleyError
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.example.mybookapplication.adapter.BookAdapter
import com.example.mybookapplication.R
import com.example.mybookapplication.util.RecyclerViewItemDecoration
import com.example.mybookapplication.data.BookData
import com.example.mybookapplication.databinding.ActivityMainBinding
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.rvBookList.addItemDecoration(
            RecyclerViewItemDecoration(
                this,
                R.drawable.divider
            )
        )

        val requestQueue = initiateRequestQueue()
        sendApiRequest(requestQueue)

    }

    private fun initiateRequestQueue(): RequestQueue {

        val cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        return requestQueue;
    }

    private fun sendApiRequest(requestQueue: RequestQueue) {
        val url = "https://www.googleapis.com/books/v1/volumes?q=jkrowling"
        binding.pbMain.visibility = View.VISIBLE
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            object : Listener<String> {
                override fun onResponse(response: String?) {
                    try {
                        binding.pbMain.visibility = View.GONE
                        val jsonObject = JSONObject(response);
                        val bookItems: JSONArray = jsonObject.getJSONArray("items");

                        val bookArrayList = ArrayList<BookData>();
                        for (i in 0..<bookItems.length()) {
                            val bookTitle = bookItems.getJSONObject(i).getJSONObject("volumeInfo")
                                .getString("title");
                            val bookAuthor = bookItems.getJSONObject(i).getJSONObject("volumeInfo")
                                .getJSONArray("authors") //as Array<String>;
                            val bookImageUrl =
                                bookItems.getJSONObject(i).getJSONObject("volumeInfo")
                                    .getJSONObject("imageLinks").getString("thumbnail")
                            val securebookImage = bookImageUrl.replace("http://", "https://");
                            val bookData =
                                BookData(bookTitle, bookAuthor.get(0) as String, securebookImage)
                            bookArrayList.add(bookData);
                        }
                        binding.rvBookList.adapter = BookAdapter(bookArrayList)
                    } catch (exception: Exception) {
                        binding.pbMain.visibility = View.GONE
                        binding.tvErrorMsg.visibility = View.VISIBLE;
                        exception.printStackTrace();
                    }
                }

            },
            object : ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {
                    binding.pbMain.visibility = View.GONE
                    binding.tvErrorMsg.visibility = View.VISIBLE
                    error?.printStackTrace()
                }
            })
        requestQueue.add(stringRequest)
    }


}