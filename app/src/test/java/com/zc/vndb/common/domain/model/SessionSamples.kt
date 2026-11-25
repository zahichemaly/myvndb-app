package com.zc.vndb.common.domain.model

object SessionSamples {

    fun session(
        userId: String = "u2",
        canReadPrivateList: Boolean = true,
        canWriteList: Boolean = true,
    ) = Session(
        userId = userId,
        username = "Yorhel",
        canReadPrivateList = canReadPrivateList,
        canWriteList = canWriteList,
    )
}
