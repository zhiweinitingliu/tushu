package com.dukang.tushu.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.stereotype.Service;

import com.dukang.tushu.dao.ImageUploadDao;
import com.dukang.tushu.domain.ImageBean;
import com.dukang.tushu.service.IImageUploadService;
import com.dukang.tushu.service.utils.FileNameUtil;
import com.dukang.tushu.service.utils.Utils;

@Service("imageUploadService")
public class ImageUploadServiceImpl implements IImageUploadService {

	@Resource
	private ImageUploadDao imageUploadDao;

	@Override
	public ImageBean uploadImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 设置上传图片的保存路径

		String filename = null;
//		String savePath = request.getServletContext().getRealPath("");
		String savePath = Utils.getImageSaveUrl(request);
		System.out.println(savePath);
		File file = new File(savePath);
		// 判断上传文件的保存目录是否存在
		if (!file.exists() && !file.isDirectory()) {
			System.out.println(savePath + "目录不存在，需要创建");
			// 创建目录
			file.mkdir();
		}
		DiskFileItemFactory factory = new DiskFileItemFactory();
		// 2、创建一个文件上传解析器
		ServletFileUpload upload = new ServletFileUpload(factory);
		upload.setHeaderEncoding("UTF-8");
		// 3、判断提交上来的数据是否是上传表单的数据
		if (!ServletFileUpload.isMultipartContent(request)) {
			// 按照传统方式获取数据
			throw new Exception("上传的不是文件类型");
		}

		try {
			List<FileItem> list = upload.parseRequest(request);
			System.out.println(list.toString());// 文件的路径 以及保存的路径
			for (FileItem item : list) {
				filename = item.getName();// 获得一个项的文件名称
				if (filename == null || filename.trim().equals("")) {// 如果為空則跳過
					continue;
				}
				// 報錯 需要過濾文件名稱 java.io.FileNotFoundException:
				// G:\测试02\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\FaceUp\WEB-INF\images\C:\Users\Ray\Pictures\2.jpeg
				// (文件名、目录名或卷标语法不正确。)

				filename = filename.substring(filename.lastIndexOf("\\") + 1);
				System.out.print("filename:" + filename);
				if (filename.substring(filename.lastIndexOf(".") + 1).equals("png")
						|| filename.substring(filename.lastIndexOf(".") + 1).equals("jpg")
						|| filename.substring(filename.lastIndexOf(".") + 1).equals("jpeg")) {

					// 文件的名称
					String preFileName = FileNameUtil.creatAloneFileName();
					// 文件的扩展
					String fileExtend = filename.substring(filename.lastIndexOf(".") + 1);
					filename = preFileName + "." + fileExtend;

					InputStream in = item.getInputStream();// 獲得上傳的輸入流
					FileOutputStream out = new FileOutputStream(savePath + filename);// 指定web-inf目錄下的images文件

					int len = 0;
					byte buffer[] = new byte[1024];
					while ((len = in.read(buffer)) > 0)// 每次讀取
					{
						out.write(buffer, 0, len);
					}
					in.close();
					out.close();
					item.delete();

					// 图片上传完毕，将图片信息写入图片表，并且将图片的完整路径和图片id返回给用户
//					Map<String, Object> map = new HashMap<>();
//					map.put("url", "tushu\\images\\");
//					map.put("image_name", filename);
//					map.put("full_url", "tushu\\images\\" + filename);
					ImageBean imageBean = new ImageBean();
					imageBean.setImage_name(filename);
					imageUploadDao.saveImageInfo(imageBean);
					return imageBean;

				} else { // 必须是图片才能上传否则失败
					throw new Exception("上传的不是图片类型");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("上传失败" + savePath);
		}
		return null;
	}

}
