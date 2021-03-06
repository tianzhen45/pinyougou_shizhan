var app = new Vue({
    el: "#account",
    data: {
        entity: {username: "",headPic: "",birthday:""},
        image_entity: {url: ""},
        day:"5",
        year:"",
        month:"",

        newPwd:""
    },

    methods: {
        // 保存用户信息
        saveUserInfo :function () {
                        // 发送异步请求
                        axios.post("user/saveUserInfo.do", this.entity).then(function (response) {
                            if (response.data.success) {
                                alert(response.data.message)
                            } else {
                                alert(response.data.message)
                            }
                        })
        },
        getUserInfo: function () {

            axios.get("user/getUserInfo.do").then(function (response) {
                app.entity = response.data.user;
                app.birthday = response.data.user.birthday;
                let date = new Date(app.birthday);
                app.year=date.getFullYear();
                app.month = date.getMonth()+1;


            });
        },
        //将上传的图片加入到对应商品描述属性
        add_image_entity: function () {
            this.entity.headPic.push(this.image_entity);
            this.image_entity = {url: ""};
        },
        //上传图片文件
        uploadFile: function () {
            // 声明一个FormData对象
            var formData = new FormData();
            // file 这个名字要和后台获取文件的名字一样; querySelector获取选择器对应的第一个元素
            formData.append('file', document.querySelector('input[type=file]').files[0]);
            //post提交
            axios({
                url: '/upload.do',
                data: formData,
                method: 'post',
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            }).then(function (response) {
                if (response.data.success) {
                    //上传成功
                    app.image_entity.url = response.data.message;
                    app.add_image_entity();
                } else {
                    //上传失败
                    alert(response.data.message);
                }
            })

        },

    },
    created() {
        this.getUserInfo();
        //加载地址列表
        this.findAddressList();
        this.findProvinceList();
    },
});