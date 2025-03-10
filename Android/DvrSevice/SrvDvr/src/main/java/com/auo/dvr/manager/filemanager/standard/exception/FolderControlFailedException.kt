package com.auo.dvr.manager.filemanager.standard.exception

import com.auo.dvr.manager.filemanager.FileManagerException
import java.io.File

class FolderControlFailedException(folder : File) : FileManagerException("Folder ${folder.absolutePath} control failed")