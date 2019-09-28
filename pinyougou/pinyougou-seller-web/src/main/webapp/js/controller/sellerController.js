var app = new Vue({
    el: "#app",
    data:{
        num: 1,
 //搜索条件对象
   searchMap:{"keywords":"","pageNo":1, "pageSize":10},
  //返回结果
  resultMap: {"itemList":[]},
   //页号数组
  pageNoList: [],
    //分页导航条中的前面3个点
    frontDot: false,
    //分页导航条中的后面3个点
    backDot: false
    },
    methods:{
        //添加到购物车
        addToCart: function (id) {
            //withCredentials表示：携带cookie等凭证信息
            axios.get("http://cart.pinyougou.com/cart/addItemToCartList.do?itemId="+id+"&num=1", {"withCredentials": true})
                .then(function (response) {
                    if (response.data.success) {
                        location.href = "http://cart.pinyougou.com";
                    } else {
                        alert(response.data.message);
                    }
                });
        },
        //根据页号查询
        queryByPageNo:function(pageNo){
            if (0 < pageNo && pageNo <= this.resultMap.totalPages) {
                this.searchMap.pageNo = pageNo;
                this.search();
            }
        },
        //构建分页导航条
        buildPagination: function () {
            this.pageNoList = [];
            //1、总页数小于等于要显示的页号数的时候；则全部页号显示。
            //起始页号
            var startPageNo = 1;
            //结束页号
            var endPageNo = this.resultMap.totalPages;
            //要在野页面中显示的总页号数；默认5
            var totalShowPageNo = 5;

            // 2、总页数大于要显示的页号数的时候：
            if(this.resultMap.totalPages > totalShowPageNo){
                //当前页左右间隔
                var interval = Math.floor(totalShowPageNo/2);

                // 起始页号：当前页号 - （要显示的页号/2）
                startPageNo = this.searchMap.pageNo - interval;
                // 结束页号：当前页号 + （要显示的页号/2）
                endPageNo = this.searchMap.pageNo + interval;


                if(startPageNo <= 0){
                    // - 如果起始页号小于等于0的时候，则置为1；结束页号要等于要显示的页号数
                    startPageNo = 1;
                    endPageNo = totalShowPageNo;
                }
                if(endPageNo > this.resultMap.totalPages) {
                    // - 如果结束页号数大于总页数则置为总页数，起始页号为总页数-要显示的页号数+1
                    endPageNo = this.resultMap.totalPages;
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
            if (endPageNo < this.resultMap.totalPages) {
                this.backDot = true;
            }

            for (let i = startPageNo; i <= endPageNo; i++) {
                this.pageNoList.push(i);
            }
        },

       //搜索商品
        search: function () {
            axios.post("item/search.do",
                this.searchMap).then(function (response) {
                app.resultMap = response.data;
                //构建分页导航条
                app.buildPagination();
            });
        } ,
        //根据参数名字获取参数
        getParameterByName: function (name) {
            return decodeURIComponent((new RegExp('[?|&]' + name + '=' + '([^&;]+?)(&|#|;|$)').exec(location.href) || [, ""])[1].replace(/\+/g, '%20')) || null
        }
        },
    created(){

        //获取浏览器地址栏中的搜索关键字
        this.searchMap.keywords = this.getParameterByName("keywords");
        this.search();
    }
});