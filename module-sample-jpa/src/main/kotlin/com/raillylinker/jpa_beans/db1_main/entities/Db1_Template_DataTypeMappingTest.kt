package com.raillylinker.jpa_beans.db1_main.entities

import com.raillylinker.converters.JsonMapConverter
import com.raillylinker.converters.MySqlSetConverter
import jakarta.persistence.*
import org.hibernate.annotations.Comment
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.locationtech.jts.geom.Geometry
import org.locationtech.jts.geom.LineString
import org.locationtech.jts.geom.Point
import org.locationtech.jts.geom.Polygon
import java.math.BigDecimal
import java.math.BigInteger
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@Entity
@Table(
    name = "data_type_mapping_test",
    catalog = "template"
)
@Comment("ORM 과 Database 간 데이터 타입 매핑을 위한 테이블")
class Db1_Template_DataTypeMappingTest(
    // 숫자 데이터
    @Column(name = "sample_tiny_int", nullable = false, columnDefinition = "TINYINT")
    @Comment("-128 ~ 127 정수 (1Byte)")
    var sampleTinyInt: Byte,
    @Column(name = "sample_tiny_int_unsigned", nullable = false, columnDefinition = "TINYINT UNSIGNED")
    @Comment("0 ~ 255 정수 (1Byte)")
    var sampleTinyIntUnsigned: Short,
    @Column(name = "sample_small_int", nullable = false, columnDefinition = "SMALLINT")
    @Comment("-32,768 ~ 32,767 정수 (2Byte)")
    var sampleSmallInt: Short,
    @Column(name = "sample_small_int_unsigned", nullable = false, columnDefinition = "SMALLINT UNSIGNED")
    @Comment("0 ~ 65,535 정수 (2Byte)")
    var sampleSmallIntUnsigned: Int,
    @Column(name = "sample_medium_int", nullable = false, columnDefinition = "MEDIUMINT")
    @Comment("-8,388,608 ~ 8,388,607 정수 (3Byte)")
    var sampleMediumInt: Int,
    @Column(name = "sample_medium_int_unsigned", nullable = false, columnDefinition = "MEDIUMINT UNSIGNED")
    @Comment("0 ~ 16,777,215 정수 (3Byte)")
    var sampleMediumIntUnsigned: Int,
    @Column(name = "sample_int", nullable = false, columnDefinition = "INT")
    @Comment("-2,147,483,648 ~ 2,147,483,647 정수 (4Byte)")
    var sampleInt: Int,
    @Column(name = "sample_int_unsigned", nullable = false, columnDefinition = "INT UNSIGNED")
    @Comment("0 ~ 4,294,967,295 정수 (4Byte)")
    var sampleIntUnsigned: Long,
    @Column(name = "sample_big_int", nullable = false, columnDefinition = "BIGINT")
    @Comment("-2^63 ~ 2^63-1 정수 (8Byte)")
    var sampleBigInt: Long,
    @Column(name = "sample_big_int_unsigned", nullable = false, columnDefinition = "BIGINT UNSIGNED")
    @Comment("0 ~ 2^64-1 정수 (8Byte)")
    var sampleBigIntUnsigned: BigInteger,
    @Column(name = "sample_float", nullable = false, columnDefinition = "FLOAT")
    @Comment("-3.4E38 ~ 3.4E38 단정밀도 부동소수점 (4Byte)")
    var sampleFloat: Float,
    @Column(name = "sample_float_unsigned", nullable = false, columnDefinition = "FLOAT UNSIGNED")
    @Comment("0 ~ 3.402823466E+38 단정밀도 부동소수점 (4Byte)")
    var sampleFloatUnsigned: Float,
    @Column(name = "sample_double", nullable = false, columnDefinition = "DOUBLE")
    @Comment("-1.7E308 ~ 1.7E308 배정밀도 부동소수점 (8Byte)")
    var sampleDouble: Double,
    @Column(name = "sample_double_unsigned", nullable = false, columnDefinition = "DOUBLE UNSIGNED")
    @Comment("0 ~ 1.7976931348623157E+308 배정밀도 부동소수점 (8Byte)")
    var sampleDoubleUnsigned: Double,
    @Column(name = "sample_decimal_p65_s10", nullable = false, columnDefinition = "DECIMAL(65, 10)")
    @Comment("p(전체 자릿수, 최대 65), s(소수점 아래 자릿수, p 보다 작거나 같아야 함) 설정 가능 고정 소수점 숫자")
    var sampleDecimalP65S10: BigDecimal,

    // 시간 데이터
    @Column(name = "sample_date", nullable = false, columnDefinition = "DATE")
    @Comment("1000-01-01 ~ 9999-12-31 날짜 데이터")
    var sampleDate: LocalDate,
    @Column(name = "sample_datetime", nullable = false, columnDefinition = "DATETIME(3)")
    @Comment("1000-01-01 00:00:00 ~ 9999-12-31 23:59:59 날짜 데이터")
    var sampleDateTime: LocalDateTime,
    @Column(name = "sample_time", nullable = false, columnDefinition = "TIME(3)")
    @Comment("-838:59:59 ~ 838:59:59 시간 데이터")
    var sampleTime: LocalTime,
    @Column(name = "sample_timestamp", nullable = false, columnDefinition = "TIMESTAMP(3)")
    @Comment("1970-01-01 00:00:01 ~ 2038-01-19 03:14:07 날짜 데이터 저장시 UTC 기준으로 저장되고, 조회시 시스템 설정에 맞게 반환")
    var sampleTimestamp: LocalDateTime,
    @Column(name = "sample_year", nullable = false, columnDefinition = "YEAR")
    @Comment("1901 ~ 2155 년도")
    var sampleYear: Int,

    // 문자 데이터
    /*
        문자 관련 데이터는 영문, 숫자를 기준으로 1 바이트 1 문자,
        그외 문자는 그 이상으로, 인커딩에 따라 달라집니다.
        UTF-8 에서 한글은 3 바이트, 특수문자는 4 바이트입니다.
     */
    @Column(name = "sample_char12", nullable = false, columnDefinition = "CHAR(12)")
    @Comment("고정 길이 문자열 (최대 255 Byte), CHAR 타입은 항상 지정된 길이만큼 공간을 차지하며, 실제 저장되는 문자열이 그보다 짧으면 빈 공간으로 패딩하여 저장합니다.")
    var sampleChar12: String,
    @Column(name = "sample_varchar12", nullable = false, columnDefinition = "VARCHAR(12)")
    @Comment("가변 길이 문자열 (최대 65,535 Byte), CHAR 과 달리 저장되는 데이터의 길이에 따라 실제 저장되는 공간이 달라집니다. CHAR 에 비해 저장 공간 활용에 강점이 있고 성능에 미비한 약점이 있습니다.")
    var sampleVarchar12: String,
    @Column(name = "sample_tiny_text", nullable = false, columnDefinition = "TINYTEXT")
    @Comment("가변 길이 문자열 최대 255 Byte")
    var sampleTinyText: String,
    @Column(name = "sample_text", nullable = false, columnDefinition = "TEXT")
    @Comment("가변 길이 문자열 최대 65,535 Byte")
    var sampleText: String,
    @Column(name = "sample_medium_text", nullable = false, columnDefinition = "MEDIUMTEXT")
    @Comment("가변 길이 문자열 최대 16,777,215 Byte")
    var sampleMediumText: String,
    @Column(name = "sample_long_text", nullable = false, columnDefinition = "LONGTEXT")
    @Comment("가변 길이 문자열 최대 4,294,967,295 Byte")
    var sampleLongText: String,

    // Bit 데이터
    @Column(name = "sample_one_bit", nullable = false, columnDefinition = "BIT(1)")
    @Comment("1 bit 값 (Boolean 으로 사용할 수 있습니다. (1 : 참, 0 : 거짓))")
    var sampleOneBit: Boolean,
    @Column(name = "sample_6_bit", nullable = false, columnDefinition = "BIT(6)")
    @Comment("n bit 값 (bit 사이즈에 따라 변수 사이즈를 맞춰 매핑)")
    var sample6Bit: Byte,

    // 컬렉션 데이터
    @Column(name = "sample_json", nullable = true, columnDefinition = "JSON")
    @Convert(converter = JsonMapConverter::class)
    @Comment("JSON 타입")
    var sampleJson: Map<String, Any?>?,
    @Column(name = "sample_enum_abc", nullable = false, columnDefinition = "ENUM('A', 'B', 'C')")
    @Enumerated(EnumType.STRING)
    @Comment("A, B, C 중 하나")
    var sampleEnumAbc: EnumAbc,
    @Column(name = "sample_set_abc", nullable = true, columnDefinition = "SET('A', 'B', 'C')")
    @Convert(converter = MySqlSetConverter::class)
    @Comment("A, B, C Set 컬렉션")
    var sampleSetAbc: Set<EnumAbc>?,

    // 공간 데이터
    @Column(name = "sample_geometry", nullable = false, columnDefinition = "GEOMETRY")
    @Comment("GEOMETRY 타입(Point, Line, Polygon 데이터 중 어느것이라도 하나를 넣을 수 있습니다.)")
    var sampleGeometry: Geometry,
    @Column(name = "sample_point", nullable = false, columnDefinition = "POINT")
    @Comment("(X, Y) 공간 좌표")
    var samplePoint: Point,
    @Column(name = "sample_linestring", nullable = false, columnDefinition = "LINESTRING")
    @Comment("직선의 시퀀스")
    var sampleLinestring: LineString,
    @Column(name = "sample_polygon", nullable = false, columnDefinition = "POLYGON")
    @Comment("다각형")
    var samplePolygon: Polygon,

    // Binary 데이터
    @Column(name = "sample_binary2", nullable = false, columnDefinition = "BINARY(2)")
    @Comment("고정 길이 이진 데이터 (최대 65535 바이트), 암호화된 값, UUID, 고정 길이 해시값 등을 저장하는 역할")
    var sampleBinary2: ByteArray,
    @Column(name = "sample_varbinary2", nullable = false, columnDefinition = "VARBINARY(2)")
    @Comment("가변 길이 이진 데이터 (최대 65535 바이트), 동적 크기의 바이너리 데이터, 이미지 등을 저장하는 역할")
    var sampleVarbinary2: ByteArray
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "uid", columnDefinition = "BIGINT")
    @Comment("행 고유값")
    var uid: Long? = null

    @Column(name = "row_create_date", nullable = false, columnDefinition = "DATETIME(3)")
    @CreationTimestamp
    @Comment("행 생성일")
    var rowCreateDate: LocalDateTime? = null

    @Column(name = "row_update_date", nullable = false, columnDefinition = "DATETIME(3)")
    @UpdateTimestamp
    @Comment("행 수정일")
    var rowUpdateDate: LocalDateTime? = null


    // ---------------------------------------------------------------------------------------------
    // <중첩 클래스 공간>
    enum class EnumAbc {
        A, B, C
    }
}