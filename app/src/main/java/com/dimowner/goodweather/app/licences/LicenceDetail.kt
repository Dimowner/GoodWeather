package com.dimowner.goodweather.app.licences

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dimowner.goodweather.R
import kotlinx.android.synthetic.main.activity_licence_detail.*

class LicenceDetail: AppCompatActivity() {

	companion object {
		const val EXTRAS_KEY_LICENCE_ITEM_POS = "licence_item_pos"
	}

	override fun onCreate(savedInstanceState: Bundle?) {
		super.onCreate(savedInstanceState)
		setContentView(R.layout.activity_licence_detail)

		val licenceTitle: String
		val licenceLocation: String?
		if (intent.hasExtra(EXTRAS_KEY_LICENCE_ITEM_POS)) {
			val pos = intent.getIntExtra(EXTRAS_KEY_LICENCE_ITEM_POS, -1)
			if (pos > -1) {
				val licences = resources.getStringArray(R.array.licences_assets_locations)
				licenceLocation = licences[pos]
				val licenceNames = resources.getStringArray(R.array.licences_names)
				licenceTitle = licenceNames[pos]
			} else {
				licenceLocation = null
				licenceTitle = ""
			}
		} else {
			licenceLocation = null
			licenceTitle = ""
		}

		if (licenceLocation != null) {
			licenceHtmlText.loadUrl(licenceLocation)
		}

		btnBack.setOnClickListener {finish()}
		txtTitle.text = licenceTitle
	}
}