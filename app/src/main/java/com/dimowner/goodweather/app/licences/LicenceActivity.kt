package com.dimowner.goodweather.app.licences

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.dimowner.goodweather.R

class LicenceActivity: AppCompatActivity() {

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_licences)

		val btnBack = findViewById<ImageButton>(R.id.btn_back)
		btnBack.setOnClickListener { finish()}


		val licenceNames = resources.getStringArray(R.array.licences_names)
		val list = findViewById<ListView>(R.id.licence_list)
		val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, android.R.id.text1, licenceNames)
		list.adapter = adapter

		list.onItemClickListener = AdapterView.OnItemClickListener { parent, view, position, id ->
			val intent = Intent(applicationContext, LicenceDetail::class.java)
			intent.putExtra(LicenceDetail.EXTRAS_KEY_LICENCE_ITEM_POS, position)
			startActivity(intent)
		}
	}
}