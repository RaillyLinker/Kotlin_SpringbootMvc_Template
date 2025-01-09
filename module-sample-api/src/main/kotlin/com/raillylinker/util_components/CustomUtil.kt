package com.raillylinker.util_components

import org.springframework.stereotype.Component

// [커스텀 유틸 함수 모음]
@Component
class CustomUtil {
    // (파일명, 경로, 확장자 분리 함수)
    // sample.jpg -> sample, jpg
    fun splitFilePath(filePath: String): FilePathParts {
        val fileName = filePath.substringBeforeLast(".", filePath) // 확장자가 없다면 전체 파일 이름이 그대로 fileName
        val extension = if (fileName != filePath) filePath.substringAfterLast(".", "") else null

        return FilePathParts(
            fileName = fileName,
            extension = extension
        )
    }

    data class FilePathParts(
        val fileName: String,
        val extension: String?
    )


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>

}