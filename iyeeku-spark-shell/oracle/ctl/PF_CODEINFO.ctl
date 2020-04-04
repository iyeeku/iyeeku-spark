load DATA
TRUNCATE
INTO TABLE PF_CODEINFO
TRAILING NULLCOLS
(
ZJ              position(1:32)              "trim(:ZJ)",
MBLXBH          position(33:96)             "trim(:MBLXBH)",
MBTMZ           position(97:351)            "trim(:MBTMZ)",
)