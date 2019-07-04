package com.nstudio.hackernews.utils

class ColorPicker {

    companion object{
        // get background color for user name icon
        fun getColor(alphabet: Char): String {
            val color: String
            when (alphabet) {
                '0', 'A' -> color = "#2E8B57"
                '1', 'B' -> color = "#6495ED"
                '2', 'C' -> color = "#FF7F50"
                '3', 'D' -> color = "#006400"
                '4', 'E' -> color = "#9932CC"
                '5', 'F' -> color = "#8FBC8F"
                '6', 'G' -> color = "#483D8B"
                '7', 'H' -> color = "#1E90FF"
                '8', 'I' -> color = "#FFA500"
                '9', 'J' -> color = "#DAA520"
                '+', 'K' -> color = "#808080"
                'L' -> color = "#FF69B4"
                'M' -> color = "#008000"
                'N' -> color = "#4B0082"
                'O' -> color = "#F08080"
                'P' -> color = "#20B2AA"
                'Q' -> color = "#778899"
                'R' -> color = "#800000"
                'S' -> color = "#9370DB"
                'T' -> color = "#3CB371"
                'U' -> color = "#7B68EE"
                'V' -> color = "#6B8E23"
                'W' -> color = "#FF4500"
                'X' -> color = "#DA70D6"
                'Y' -> color = "#DB7093"
                'Z' -> color = "#4682B4"
                else -> color = "#A0522D"
            }
            return color
        }

    }

}