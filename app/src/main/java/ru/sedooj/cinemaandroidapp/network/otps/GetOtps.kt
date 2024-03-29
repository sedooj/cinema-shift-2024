package ru.sedooj.cinemaandroidapp.network.otps

import ru.sedooj.cinemaandroidapp.network.Client

interface GetOtps : Client {

    suspend fun getOtpsCode(getOtpInput: GetOtpInput): GetOtpsResponse?

}