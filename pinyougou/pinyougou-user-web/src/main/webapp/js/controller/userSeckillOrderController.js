var app = new Vue({
    el: "#app",
    data: {
        //当前页
        pageNum:1,
        //页大小
        pageSize:2,
        //总页数
        pages:0,
        
        //返回结果
        resultMap:{"seckillOrderList":[]},

        //页号数组
        pageNoList: [],
        //分页导航条中的前面3个点
        frontDot: false,
        //分页导航条中的后面3个点
        backDot: false,
        //选择的id数组
        ids: [],
        //全选的复选框
        checkAll: false
    },
    //监控数据属性的变化
    watch:{
        ids:{
            //开启深度监控；可以监控里面具体元素的改变
            deep: true,
            //处理方法
            handler: function (newValue, oldValue) {
                if (this.ids.length == this.resultMap.seckillOrderList.length && this.resultMap.seckillOrderList.length>0) {
                    this.checkAll = true;
                } else {
                    this.checkAll = false;
                }
            }
        }
    },
    methods: {
        //批量删除
        deleteSeckillOrder: function () {
            if (this.ids.length == 0) {
                alert("请先选择要删除的记录！");
                return;
            }
            //确认是否要删除
            //confirm 如果点击 确定 则返回true
            if(confirm("确定要删除选中了的那些记录吗？")){
                axios.get("user/deleteSeckillOrder.do?ids=" + this.ids).then(function (response) {
                    if(response.data.success){
                        app.queryByPageNo(1);
                        //清空选择的id
                        app.ids = [];
                    } else {
                        alert(response.data.message);
                    }
                });
            }
        },
        //选择全部
        selectAll: function () {
            if (!this.checkAll) {
                this.ids = [];
                //选中
                for (let i = 0; i < this.resultMap.seckillOrderList.length; i++) {
                    var vo = this.resultMap.seckillOrderList[i];
                    this.ids.push(vo.seckillOrder.id);
                }
            } else {
                //将所有的选择反选
                this.ids = [];
            }
        },

        //构建分页导航条
        buildPagination: function () {
            this.pageNoList = [];
            //1、总页数小于等于要显示的页号数的时候；则全部页号显示。
            //起始页号
            var startPageNo = 1;
            //结束页号
            var endPageNo = this.resultMap.pages;
            //要在野页面中显示的总页号数；默认5
            var totalShowPageNo = 3;

            // 2、总页数大于要显示的页号数的时候：
            if(this.resultMap.pages > totalShowPageNo){
                //当前页左右间隔
                var interval = Math.floor(totalShowPageNo/2);

                // 起始页号：当前页号 - （要显示的页号/2）
                startPageNo = this.pageNum - interval;
                // 结束页号：当前页号 + （要显示的页号/2）
                endPageNo = this.pageNum + interval;


                if(startPageNo <= 0){
                    // - 如果起始页号小于等于0的时候，则置为1；结束页号要等于要显示的页号数
                    startPageNo = 1;
                    endPageNo = totalShowPageNo;
                }
                if(endPageNo > this.resultMap.pages) {
                    // - 如果结束页号数大于总页数则置为总页数，起始页号为总页数-要显示的页号数+1
                    endPageNo = this.resultMap.pages;
                    startPageNo = endPageNo - totalShowPageNo +1;
                }
            }

            this.frontDot = false;
            this.backDot = false;

            //前面3个点
            if (startPageNo > 1) {
                this.frontDot = true;
            }
            //后面3个点
            if (endPageNo < this.resultMap.pages) {
                this.backDot = true;
            }

            for (let i = startPageNo; i <= endPageNo; i++) {
                this.pageNoList.push(i);
            }
        },
        //根据页号查询
        queryByPageNo:function(pageNo){
            if (0 < pageNo && pageNo <= this.pages) {
                this.pageNum = pageNo;
                this.findUserSeckillOrder();
            }
        },
        //用户秒杀订单
        findUserSeckillOrder:function () {
            axios.post("user/findUserSeckillOrder.do?pageNum="+ this.pageNum + "&pageSize=" + this.pageSize).then(function (response) {
                app.resultMap = response.data;
                app.pageNum = app.resultMap.pageNum;
                app.pages = app.resultMap.pages;
                app.buildPagination();
            });
        },


    },
    created() {
        this.findUserSeckillOrder();
    }
});