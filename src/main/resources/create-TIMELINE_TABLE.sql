CREATE TABLE IF NOT EXISTS datalake_tvshoptime.tvst_timeline (tln_dia STRING,tln_hora STRING,tln_blc_id STRING,tln_tipo STRING) PARTITIONED BY (dt_dia STRING) STORED AS PARQUET 
