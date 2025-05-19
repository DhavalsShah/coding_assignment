package com.example.mybookapplication

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.VolleyError
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.example.mybookapplication.data.BookData
import org.json.JSONArray
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerViewBook : RecyclerView;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        recyclerViewBook = findViewById<RecyclerView>(R.id.rv_book_list);
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }

        val requestQueue = initiateRequestQueue()
        sendApiRequest(requestQueue)

    }

    private fun initiateRequestQueue()  : RequestQueue {

        // Instantiate the cache
        val cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap

// Set up the network to use HttpURLConnection as the HTTP client.
        val network = BasicNetwork(HurlStack())
        // Instantiate the RequestQueue with the cache and network. Start the queue.
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        return requestQueue;
    }

    private fun sendApiRequest(requestQueue : RequestQueue) {
        val url = "https://www.googleapis.com/books/v1/volumes?q=jkrowling"
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            object : Listener<String> {
                override fun onResponse(response: String?) {
                    Log.d("response", response!!)
                    val jsonObject = JSONObject(response);
                    val bookItems : JSONArray = jsonObject.getJSONArray("items");

                    val bookArrayList = ArrayList<BookData>();
                    for (i in 0..<bookItems.length()) {
                        val bookTitle = bookItems.getJSONObject(i).getJSONObject("volumeInfo").getString("title");
                        val bookAuthor = bookItems.getJSONObject(i).getJSONObject("volumeInfo").getJSONArray("authors") //as Array<String>;
                        Log.d("bookTitle:", bookTitle);
                        Log.d("bookAuthor:", bookAuthor.get(0) as String);
                        val bookData = BookData(bookTitle, bookAuthor.get(0) as String)
                        bookArrayList.add(bookData);
                    }
                    recyclerViewBook.adapter = BookAdapter(bookArrayList)
                }
            },
            object : ErrorListener {
                override fun onErrorResponse(error: VolleyError?) {}
            })
        requestQueue.add(stringRequest)
    }


}