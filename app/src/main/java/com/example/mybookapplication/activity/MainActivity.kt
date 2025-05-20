package com.example.mybookapplication.activity

import android.app.Dialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response.ErrorListener
import com.android.volley.Response.Listener
import com.android.volley.VolleyError
import com.android.volley.toolbox.BasicNetwork
import com.android.volley.toolbox.DiskBasedCache
import com.android.volley.toolbox.HurlStack
import com.android.volley.toolbox.StringRequest
import com.bumptech.glide.Glide
import com.example.mybookapplication.R
import com.example.mybookapplication.adapter.BookAdapter
import com.example.mybookapplication.util.RecyclerViewItemDecoration
import com.example.mybookapplication.data.BookData
import com.example.mybookapplication.databinding.ActivityMainBinding
import com.example.mybookapplication.databinding.DialogBookInfoBinding
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
        binding.tvErrorMsg.setOnClickListener { sendApiRequest(requestQueue); }
    }

    private fun initiateRequestQueue(): RequestQueue {

        val cache = DiskBasedCache(cacheDir, 1024 * 1024) // 1MB cap
        val network = BasicNetwork(HurlStack())
        val requestQueue = RequestQueue(cache, network).apply {
            start()
        }
        return requestQueue;
    }

    /**
     * To fetch the book list and populate data in recyclerview
     */
    private fun sendApiRequest(requestQueue: RequestQueue) {
        val url = "https://www.googleapis.com/books/v1/volumes?q=jkrowling"
        binding.pbMain.visibility = View.VISIBLE
        binding.tvErrorMsg.visibility = View.GONE
        val stringRequest = StringRequest(
            Request.Method.GET, url,
            { response ->
                try {
                    binding.pbMain.visibility = View.GONE
                    val jsonObject = JSONObject(response);
                    val bookItems: JSONArray = jsonObject.getJSONArray("items");

                    val bookArrayList = ArrayList<BookData>();
                    for (i in 0..<bookItems.length()) {
                        val bookTitle = bookItems.getJSONObject(i).getJSONObject("volumeInfo")
                            .getString("title");
                        val bookAuthor = bookItems.getJSONObject(i).getJSONObject("volumeInfo")
                            .getJSONArray("authors")
                        val bookImageUrl =
                            bookItems.getJSONObject(i).getJSONObject("volumeInfo")
                                .getJSONObject("imageLinks").getString("thumbnail")
                        val securebookImage = bookImageUrl.replace("http://", "https://");

                        val pageCount = bookItems.getJSONObject(i).getJSONObject("volumeInfo")
                            .getInt("pageCount")
                        val printType = bookItems.getJSONObject(i).getJSONObject("volumeInfo")
                            .getString("printType")
                        val description = bookItems.getJSONObject(i).getJSONObject("volumeInfo")
                            .getString("description")

                        val bookData =
                            BookData(
                                bookTitle,
                                bookAuthor.get(0) as String,
                                securebookImage,
                                pageCount,
                                printType,
                                description
                            )
                        bookArrayList.add(bookData);
                    }
                    binding.rvBookList.adapter = BookAdapter(
                        bookArrayList,
                        { clickedPosition -> onBookClicked(bookArrayList[clickedPosition]) })

                } catch (exception: Exception) {
                    binding.pbMain.visibility = View.GONE
                    binding.tvErrorMsg.visibility = View.VISIBLE;
                    exception.printStackTrace();
                }
            }
        ) { error ->
            binding.pbMain.visibility = View.GONE
            binding.tvErrorMsg.visibility = View.VISIBLE
            error?.printStackTrace()
        }
        requestQueue.add(stringRequest)
    }

    /**
     * This opens the clicked book info popup
     * @param book : consists clicked book details
     */
    private fun onBookClicked(book: BookData) {
        val dialog = Dialog(this@MainActivity)
        val dialogBookInfoBinding = DialogBookInfoBinding.inflate(layoutInflater)
        dialog.setContentView(dialogBookInfoBinding.root)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.setBackgroundDrawable(
            AppCompatResources.getDrawable(
                this,
                R.drawable.dialog_background_insets
            )
        )

        Glide.with(dialogBookInfoBinding.root.context).load(book.bookImage)
            .into(dialogBookInfoBinding.ivBookImage)
        dialogBookInfoBinding.tvBookName.text = book.bookName
        dialogBookInfoBinding.tvAuthorName.text = book.bookAuthor
        dialogBookInfoBinding.tvPageCount.text = "${book.pageCount} Pages"
        dialogBookInfoBinding.tvPrintType.text = book.printType
        dialogBookInfoBinding.tvShortSummary.text = book.description
        dialogBookInfoBinding.ivCloseBtn.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show();
    }
}