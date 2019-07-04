package com.nstudio.hackernews.utils

import android.text.Html
import android.os.Build
import android.text.SpannableString
import android.text.Spanned



class FormatHtml{

    companion object{
        // function to format html tags in comment
        @SuppressWarnings("deprecation")
        fun fromHtml(html: String?): Spanned {
            return when {
                html == null ->
                    SpannableString("")
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.N ->
                    Html.fromHtml(html.trim(), Html.FROM_HTML_MODE_LEGACY)
                else -> Html.fromHtml(html.trim())
            }
        }
    }


}