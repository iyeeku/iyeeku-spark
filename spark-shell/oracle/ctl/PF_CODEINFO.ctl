load DATA
TRUNCATE
INTO TABLE PF_CODEINFO
TRAILING NULLCOLS
(
ZJ              position(1:32)              "trim(:ZJ)",
MBLXBH          position(33:96)             "trim(:MBLXBH)",
MBTMZ           position(97:351)            "trim(:MBTMZ)",
SJLX            position(352:383)           "trim(:SJLX)",
MBTMMS          position(384:638)           "trim(:MBTMMS)",
SJSXSY          position(639:648)           "trim(:SJSXSY)",
GJDBM           position(649:680)           "trim(:GJDBM)",
FJDBM           position(681:712)           "trim(:FJDBM)",
BMJB            position(713:718)           "trim(:BMJB)",
QLJ             position(719:973)           "trim(:QLJ)",
SFKJ            position(974:974)           "trim(:SFKJ)",
SJSYGJHXX       position(975:994)           "trim(:SJSYGJHXX)",
JLZT            position(995:995)           "trim(:JLZT)",
KZZD            position(996:1059)          "trim(:KZZD)"
)