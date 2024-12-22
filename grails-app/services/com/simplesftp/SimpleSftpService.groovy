package com.simplesftp

import com.jcraft.jsch.Channel
import com.jcraft.jsch.ChannelSftp
import com.jcraft.jsch.JSch
import com.jcraft.jsch.Session

import grails.gorm.transactions.Transactional

@Transactional
class SimpleSftpService {
	def grailsApplication
	
	def uploadFile(InputStream inputStream, String fileName) {
		connect { ChannelSftp sftp ->
			sftp.put inputStream, fileName
		}
	}

	def downloadFile(String fileName) {
		connect({ ChannelSftp sftp ->
			File outputFile = File.createTempFile(fileName,'')
			outputFile?.newOutputStream() << sftp.get(fileName)
			outputFile
		}, false)
	}

	def removeFile(String fileName) throws Throwable {
		connect { ChannelSftp sftp ->
			sftp.rm fileName
		}
	}

	def renameFile(String oldPath, String newPath) {
		connect { ChannelSftp sftp ->
			sftp.rename oldPath, newPath
		}
	}
	
	def createDir(String dirName) {
		connect { ChannelSftp sftp ->
			sftp.mkdir dirName
		}
	}

	private def connect(Closure c, boolean disconnect = true) {
		Session session = null
		ChannelSftp sftp = null
		String server = grailsApplication.config.getProperty("simpleSftp.server")
		String username = grailsApplication.config.getProperty("simpleSftp.username")
		String password = grailsApplication.config.getProperty("simpleSftp.password")
		String remoteDir = grailsApplication.config.getProperty("simpleSftp.remoteDir")
		int port = grailsApplication.config.getProperty("simpleSftp.port").toInteger()
		String keyFilePath = grailsApplication.config.getProperty("simpleSftp.keyFilePath")
		Boolean throwException = grailsApplication.config.getProperty("simpleSftp.throwException")

		try {
			JSch jSch = new JSch()
			session = jSch.getSession username, server, port
			session.setConfig "StrictHostKeyChecking", "no"
			
			if (password) {
				session.password = password
			} else {
				File keyFile = new File(keyFilePath)
				jSch.addIdentity(keyFile?.absolutePath)
			}
			
			session.connect()
			Channel channel = session.openChannel "sftp"
			channel.connect()
			sftp = channel as ChannelSftp
			sftp.cd remoteDir
			c.call sftp
		} catch (Exception e) {
			log.error(e)
            if (throwException) {
                throw e
            }
		} finally {
			if (disconnect) {
				sftp?.exit()
				session?.disconnect()
			}
		}
	}
}
