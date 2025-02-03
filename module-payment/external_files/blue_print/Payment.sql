CREATE TABLE `payment_request_detail_bank_transfer` (
	`uid`	BIGINT	NOT NULL	COMMENT '행 고유키',
	`row_create_date`	DATETIME(3)	NOT NULL	COMMENT '행 생성일',
	`row_update_date`	DATETIME(3)	NOT NULL	COMMENT '행 수정일',
	`row_delete_date_str`	VARCHAR(50)	NOT NULL	DEFAULT /	COMMENT '행 삭제일(yyyy_MM_dd_T_HH_mm_ss_SSS_z, 삭제되지 않았다면 /)',
	`payment_request_uid`	BIGINT	NOT NULL	COMMENT '결제 요청 정보 고유번호',
	`receive_bank_name`	VARCHAR(60)	NOT NULL	COMMENT '입금 받을 은행명',
	`receive_bank_account`	VARCHAR(60)	NOT NULL	COMMENT '입금 받을 은행 계좌번호',
	`depositor_name`	VARCHAR(60)	NULL	COMMENT '입금자 이름',
	`payment_check_deadline`	DATETIME	NOT NULL	COMMENT '결제 확인 기한(이 기한이 지날 때 까지 결제 완료 처리가 되지 않았다면 결제 취소로 간주합니다. 다만, 결제 테이블의 결제 완료일 설정이 우선됩니다.)'
);

CREATE TABLE `payment_refund_request` (
	`uid`	BIGINT	NOT NULL	COMMENT '행 고유키',
	`row_create_date`	DATETIME(3)	NOT NULL	COMMENT '행 생성일',
	`row_update_date`	DATETIME(3)	NOT NULL	COMMENT '행 수정일',
	`row_delete_date_str`	VARCHAR(50)	NOT NULL	DEFAULT /	COMMENT '행 삭제일(yyyy_MM_dd_T_HH_mm_ss_SSS_z, 삭제되지 않았다면 /)',
	`payment_request_uid`	BIGINT	NOT NULL	COMMENT '결제 요청 정보 고유번호',
	`refund_amount`	DECIMAL(15, 2)	NULL	COMMENT '환불 금액(통화 코드는 결제 정보 테이블과 동일합니다. null 이라면 전액 환불입니다.)',
	`refund_reason`	VARCHAR(300)	NOT NULL	COMMENT '환불 요청 이유',
	`refund_fail_reason`	VARCHAR(300)	NULL	COMMENT '환불 실패 이유(환불 실패라면 Not Null)',
	`refund_end_datetime`	DATETIME(3)	NULL	COMMENT '환불 프로세스 종결일시(refund_fail_reason  이 null 이라면 완료일, not null 이라면 실패일)'
);

CREATE TABLE `payment_request_detail_toss_payments` (
	`uid`	BIGINT	NOT NULL	COMMENT '행 고유키',
	`row_create_date`	DATETIME(3)	NOT NULL	COMMENT '행 생성일',
	`row_update_date`	DATETIME(3)	NOT NULL	COMMENT '행 수정일',
	`row_delete_date_str`	VARCHAR(50)	NOT NULL	DEFAULT /	COMMENT '행 삭제일(yyyy_MM_dd_T_HH_mm_ss_SSS_z, 삭제되지 않았다면 /)',
	`payment_request_uid`	BIGINT	NOT NULL	COMMENT '결제 요청 정보 고유번호',
	`toss_payment_key`	VARCHAR(200)	NOT NULL	COMMENT '결제의 키값입니다. 최대 길이는 200자입니다. 결제를 식별하는 역할로, 중복되지 않는 고유한 값입니다.',
	`toss_order_id`	VARCHAR(64)	NOT NULL	COMMENT '주문번호입니다. 결제 요청에서 내 상점이 직접 생성한 영문 대소문자, 숫자, 특수문자 -, _로 이루어진 6자 이상 64자 이하의 문자열입니다. 각 주문을 식별하는 역할입니다.'
);

CREATE TABLE `payment_request` (
	`uid`	BIGINT	NOT NULL	COMMENT '행 고유키',
	`row_create_date`	DATETIME(3)	NOT NULL	COMMENT '행 생성일',
	`row_update_date`	DATETIME(3)	NOT NULL	COMMENT '행 수정일',
	`row_delete_date_str`	VARCHAR(50)	NOT NULL	DEFAULT /	COMMENT '행 삭제일(yyyy_MM_dd_T_HH_mm_ss_SSS_z, 삭제되지 않았다면 /)',
	`payment_code`	VARCHAR(100)	NOT NULL	COMMENT '결제 코드입니다. 외부 모듈에서 결제를 의뢰할 때에 구분을 위해 입력하는 정보로, {모듈 고유값}_{모듈 내 고유값} 으로 이루어집니다.',
	`payment_detail_type`	TINYINT UNSIGNED	NOT NULL	COMMENT '결제 타입. 결제 상세 테이블의 종류를 의미합니다. (1 : 수동 계좌이체, 2 : 토스 페이)',
	`payment_amount`	DECIMAL(15, 2)	NOT NULL	COMMENT '결제 금액',
	`payment_currency_code`	CHAR(3)	NOT NULL	COMMENT '결제 금액 통화 코드(IOS 4217, ex : KRW, USD, EUR...)',
	`payment_reason`	VARCHAR(300)	NOT NULL	COMMENT '결제이유',
	`payment_fail_reason`	VARCHAR(300)	NULL	COMMENT '결제 실패 이유(결제 실패라면 Not Null)',
	`payment_end_datetime`	DATETIME(3)	NULL	COMMENT '결제 프로세스 종결일시(payment_fail_reason 이 null 이라면 완료일, not null 이라면 실패일)'
);

ALTER TABLE `payment_request_detail_bank_transfer` ADD CONSTRAINT `PK_PAYMENT_REQUEST_DETAIL_BANK_TRANSFER` PRIMARY KEY (
	`uid`
);

ALTER TABLE `payment_refund_request` ADD CONSTRAINT `PK_PAYMENT_REFUND_REQUEST` PRIMARY KEY (
	`uid`
);

ALTER TABLE `payment_request_detail_toss_payments` ADD CONSTRAINT `PK_PAYMENT_REQUEST_DETAIL_TOSS_PAYMENTS` PRIMARY KEY (
	`uid`
);

ALTER TABLE `payment_request` ADD CONSTRAINT `PK_PAYMENT_REQUEST` PRIMARY KEY (
	`uid`
);

