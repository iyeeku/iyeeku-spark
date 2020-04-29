#程序名称：JChar.dec
#程序描述：字符串操作函数块
#程序类型：脚本
#执行环境Shell
SYS_RUN_CMD="sh $0 $*"
export SYS_DIR_BASE="/iyeeku/app/ctm"
SYS_EXIT_CODE=0
cd ${SYS_DIR_BASE}
if [ $? -ne 0 ]; then
    echo "无法切换到工作目录：[SYS_DIR_BASE]"
    exit 1
fi

. ./truck/sys_func.dec
. ./truck/sys_job.dec
. ./truck/sys_log.dec

trap "JSysJob abort" 1 2 3 15
JSysJob init $*

JsysJob before $*

JsysJob run $*

JsysJob after $*

JSysLog crBLog
JSysLog crJLog
exit ${SYS_EXIT_CODE}
