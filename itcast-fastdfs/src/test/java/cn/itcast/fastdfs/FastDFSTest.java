package cn.itcast.fastdfs;

import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.IOException;

public class FastDFSTest {

    @Test
    public void test() throws Exception {
        //配置文件
        String conf_filename = ClassLoader.getSystemResource("fastdfs/tracker.conf").getPath();
        //1、设置追踪服务器的信息；
        ClientGlobal.init(conf_filename);
        //2、创建一个追踪服务器客户端TrackerClient；
        TrackerClient trackerClient = new TrackerClient();
        //3、由TrackerClient获取TrackerServer；
        TrackerServer trackerServer = trackerClient.getConnection();
        //4、创建存储服务器StorageServer，可以为空；上述的追踪服务器足够获取要的信息；
        StorageServer storageServer = null;
        //5、利用TrackerServer和StroageServer创建StoageClient；
        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        //6、上传图片并处理返回结果
        /**
         * 参数1：文件路径
         * 参数2：文件的扩展名（后缀）
         * 参数3：文件的信息
         * 返回的结果：
         * group1 组名
         * M00/00/00/wKgMqF1zgYuADdc7AABw0se6LsY114.jpg 文件相对路径
         * http://192.168.12.168/group1/M00/00/00/wKgMqF1zgYuADdc7AABw0se6LsY114.jpg
         */
        String[] upload_files = storageClient.upload_file("D:\\itcast\\pics\\575968fcN2faf4aa4.jpg", "jpg", null);
        if (upload_files != null && upload_files.length > 0) {
            for (String str : upload_files) {
                System.out.println(str);
            }

            //获取存储服务器ip
            String group = upload_files[0];
            String filename = upload_files[1];
            ServerInfo[] serverInfos = trackerClient.getFetchStorages(trackerServer, group, filename);
            for (ServerInfo serverInfo : serverInfos) {
                System.out.println(" ip = " + serverInfo.getIpAddr() + "； port = " + serverInfo.getPort());
            }

            String url = "http://"+serverInfos[0].getIpAddr() + "/" + group + "/" + filename;

            System.out.println("可以访问的图片地址：" + url);
        }
    }
}
