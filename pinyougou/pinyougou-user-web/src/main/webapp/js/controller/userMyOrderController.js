var app = new Vue({
    el:"#account",
    data:{
        //当前页
        pageNum: 1,
        //页大小
        pageSize: 2,
        //总页数
        pages:"",
        //返回结果
        resultMap: {"myOrderList":[]},
        //页号数组
        pageNoList: [],
        //分页导航条中的前面3个点
        frontDot: false,
        //分页导航条中的后面3个点
        backDot: false,


    },
    methods:{
        search:function () {
            axios.get("user/findOrder.do?pageNum=" + this.pageNum +"&pageSize=" + this.pageSize).then(function (response) {
                app.resultMap = response.data;
                app.pageNum = response.data.pageNum;
                app.pages = response.data.pages;
                //加载分页导航条
                app.buildPagination()
            })
        },
        //根据页号查询
        queryByPageNo:function(pageNum){
            if (0 < pageNum && pageNum <= this.resultMap.pages) {
                this.pageNum = pageNum;
                this.search();
            }
        },
        //取消订单
        deleteOrder: function(orderId){
            axios.get("user/deleteOrder.do?orderId="+orderId).then(function (response) {
                    alert(response.data.message);
                this.search()
            })
        },
        //构建分页导航条
        buildPagination: function () {
            this.pageNoList = [];
            //1、总页数小于等于要显示的页号数的时候；则全部页号显示。
            //起始页号
            var startPageNo = 1;
            //结束页号
            var endPageNo = this.resultMap.pages;
            //要在页面中显示的总页号数；默认5
            var pagesShowPageNo = 5;

            // 2、总页数大于要显示的页号数的时候：
            if(this.resultMap.pages > pagesShowPageNo){
                //当前页左右间隔
                var interval = Math.floor(pagesShowPageNo/2);

                // 起始页号：当前页号 - （要显示的页号/2）
                startPageNo = this.resultMap.pageNum - interval;
                // 结束页号：当前页号 + （要显示的页号/2）
                endPageNo = this.resultMap.pageNum + interval;


                if(startPageNo <= 0){
                    // - 如果起始页号小于等于0的时候，则置为1；结束页号要等于要显示的页号数
                    startPageNo = 1;
                    endPageNo = pagesShowPageNo;
                }
                if(endPageNo > this.resultMap.pages) {
                    // - 如果结束页号数大于总页数则置为总页数，起始页号为总页数-要显示的页号数+1
                    endPageNo = this.resultMap.pages;
                    startPageNo = endPageNo - pagesShowPageNo +1;
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
        }
    },
    created(){
        this.search()
    }
});