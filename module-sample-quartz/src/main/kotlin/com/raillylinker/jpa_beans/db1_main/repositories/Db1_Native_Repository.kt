package com.raillylinker.jpa_beans.db1_main.repositories

import com.raillylinker.jpa_beans.db1_main.entities.Db1_QuartzMetadata_QRTZ_BLOB_TRIGGERS
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

// 주의 : NativeRepository 의 반환값으로 기본 Entity 객체는 매핑되지 않으므로 OutputVo Interface 를 작성하여 사용할것.
// Output Interface 변수에 is 로 시작되는 변수는 매핑이 안되므로 사용하지 말것.

/* SQL Select 의 실행 순서
    1. FROM 절: 데이터베이스에서 데이터를 추출할 테이블이나 뷰를 지정합니다.
    2. JOIN 절: FROM 절에서 얻어온 테이블에 조건에 맞게 결합합니다. 여러 JOIN 절의 실행 순서는, 쿼리문 내 JOIN 작성 순서대로 진행됩니다.
    3. WHERE 절: FROM 절에서 지정된 테이블에서 필터링을 수행합니다. 조건에 맞지 않는 행을 제외합니다.
    4. GROUP BY 절: 그룹별로 데이터를 집계하기 위해 데이터를 그룹화합니다. GROUP BY 절에 지정된 열을 기준으로 행을 그룹화하고, 이후 집계 함수를 사용하여 각 그룹에 대한 집계를 계산합니다.
    5. HAVING 절: GROUP BY 절에서 그룹화된 결과에 대한 조건을 지정합니다. HAVING 절은 WHERE 절과 유사하지만, 그룹별로 조건을 적용하여 그룹을 필터링합니다.
    6. SELECT 절: 쿼리 결과 집합에 포함할 열을 선택합니다. GROUP BY 절과 함께 사용할 때는 집계 함수를 포함할 수 있습니다.
    7. ORDER BY 절: 결과 집합을 정렬합니다. ORDER BY 절은 SELECT 문이 실행된 후에 적용됩니다.
 */
@Repository
interface Db1_Native_Repository : JpaRepository<Db1_QuartzMetadata_QRTZ_BLOB_TRIGGERS, Long> {
}