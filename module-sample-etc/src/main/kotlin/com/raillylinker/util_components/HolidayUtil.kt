package com.raillylinker.util_components

import org.springframework.stereotype.Component
import org.w3c.dom.Element
import org.w3c.dom.Node
import java.io.BufferedReader
import java.io.ByteArrayInputStream
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory


// [휴일 관련 유틸 오브젝트]
@Component
class HolidayUtil {
    // (공공 데이터 포털에서 해당 년도의 모든 공휴일 데이터 리스트 가져오기)
    fun fetchHolidays(year: Int): List<HolidayItem> {
        // 공공 데이터 포털 공휴일 리스트 API
        val baseUrl = "https://apis.data.go.kr/B090041/openapi/service/SpcdeInfoService/getRestDeInfo"
        // 공공 데이터 포털 공휴일 리스트 API 시크릿 키
        val serviceKey =
            "mAclq2ftu%2FTfOT7uWxCyRuCJ%2FzZ%2B3eF%2FNPGwsZIO6ok3sJsZUtNhMinZFRMraedPejtFW%2F%2BKG52Fsvb5YgFK1w%3D%3D" // Replace with your actual service key

        val holidayList: MutableList<HolidayItem> = ArrayList()
        var pageNo = 1
        var totalCount: Int

        // API 가 페이징 처리 되어있으므로 첫페이지부터 해서 마지막 페이지 까지 반복
        do {
            // URL 파라미터 합성
            val url = String.format("%s?serviceKey=%s&solYear=%d&pageNo=%d", baseUrl, serviceKey, year, pageNo)

            // Get 메소드 요청 객체 생성
            val connection: HttpURLConnection = URL(url).openConnection() as HttpURLConnection
            connection.setRequestMethod("GET")

            // Response 읽기
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            val response = StringBuilder()
            var line: String?
            while ((reader.readLine().also { line = it }) != null) {
                response.append(line)
            }
            reader.close()

            // XML response 파싱
            val factory = DocumentBuilderFactory.newInstance()
            val builder = factory.newDocumentBuilder()
            val document = builder.parse(ByteArrayInputStream(response.toString().toByteArray()))

            val items = document.getElementsByTagName("item")
            for (i in 0 until items.length) {
                val item = items.item(i)
                val holiday = HolidayItem(
                    getTagValue("locdate", item),
                    getTagValue("seq", item)?.toInt(),
                    getTagValue("dateKind", item),
                    getTagValue("isHoliday", item),
                    getTagValue("dateName", item)
                )
                holidayList.add(holiday)
            }

            totalCount = getTagValue("totalCount", document.getElementsByTagName("body").item(0))!!.toInt()
            pageNo++
        } while (holidayList.size < totalCount)

        // 반복 제약 설정
        return holidayList
    }

    private fun getTagValue(tagName: String, node: Node): String? {
        if (node.nodeType == Node.ELEMENT_NODE) {
            val element = node as Element
            val tag = element.getElementsByTagName(tagName).item(0)
            if (tag != null && tag.firstChild != null) {
                return tag.firstChild.nodeValue
            }
        }
        return null
    }

    data class HolidayItem(
        // 날짜(ex : 20150301)
        val locdate: String?,
        // 순번(ex : 1)
        val seq: Int?,
        // 종류(ex : 00)
        val dateKind: String?,
        // 공공기관 휴일여부(ex : Y)
        val isHoliday: String?,
        // 명칭(ex : 삼일절)
        val dateName: String?
    )
}