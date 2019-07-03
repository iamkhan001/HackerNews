package com.nstudio.hackernews.model

data class QueryResponse(val event: Event){

    var msg:String = ""

    constructor(event: Event,msg:String) : this(event) {
        this@QueryResponse.msg = msg
    }

}