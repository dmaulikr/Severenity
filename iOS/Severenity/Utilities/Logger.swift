//
//  Logger.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 24.11.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import Foundation

class Log: NSObject {
    
    /// Logs info message
    class func info(message: String, sender: Any, detailed: Bool = false) {
        DispatchQueue.global(qos: .background).async {
        var logContent = ""
        if detailed {
            logContent = "----------------------------------------" +
                    "INFO: \(message)" +
                    "EXECUTOR: \(#file) :\(#line)" +
                    "CALLER: \(String(describing: sender)) :\(#column)" +
                    "TIMESTAMP: \(Date().iso8601)" +
                    "----------------------------------------"
        } else {
            logContent = "INFO: \(message) FROM: \(String(describing: sender))"
        }
        print(logContent)
        toFile(content: logContent)
        }
    }
    
    /// Logs error message
    class func error(message: String, sender: Any, detailed: Bool = false) {
        DispatchQueue.global(qos: .background).async {
        var logContent = ""
        if detailed {
            logContent = "----------------------------------------" +
                    "ERROR: \(message)" +
                    "EXECUTOR: \(#file) :\(#line)" +
                    "CALLER: \(String(describing: sender)) :\(#column)" +
                    "TIMESTAMP: \(Date().iso8601)" +
                    "----------------------------------------"
        } else {
            logContent = "ERROR: \(message) FROM: \(String(describing: sender))"
        }
        print(logContent)
        toFile(content: logContent)
        }
    }
    
    // MARK: Working with file
    
    /// Writes String to file (with name kLogFileName) located in Documents directory.
    /// If the file already exists this method checks its size and appends new info to the end.
    /// If not then file is created.
    private class func toFile(content: String) {
        let data = (content + "\n").data(using: String.Encoding.utf8)!
        if let file = kDocumentDirPath?.appendingPathComponent(kLogFileName) {
            if FileManager.default.fileExists(atPath: file.path) && checkFileSizeAt(path: file.path) {
                do  {
                    let fileHandle = try FileHandle(forWritingTo: file)
                    fileHandle.seekToEndOfFile()
                    fileHandle.write(data)
                } catch {
                    print("ERROR: Writing log to file failed: \(error)")
                }
            } else {
                FileManager.default.createFile(atPath: file.path, contents: data, attributes: nil)
            }
        }
    }
    
    /// Tries to read data from log file (with name kLogFileName) located in Documents directory to String?
    /// If read fails then error is printed to the console.
    private class func fromFile() -> String? {
        var content: String?
        if let file = kDocumentDirPath?.appendingPathComponent(kLogFileName) {
            do {
                content = try String(contentsOf: file, encoding: String.Encoding.utf8)
            } catch {
                print("ERROR: Reading log from file failed: \(error)")
            }
        }
        return content
    }
    
    /// Checks size of log file (with name kLogFileName). If it's <10mb then returns true.
    /// If not then file is removed and true is returned.
    /// In case of size check fail, corresponding error is printed to the console and false is returned
    private class func checkFileSizeAt(path: String) -> Bool {
        var fileSize : UInt64 = 0
        do {
            let fileAttributes = try FileManager.default.attributesOfItem(atPath: path)
            fileSize = fileAttributes[FileAttributeKey.size] as! UInt64
        } catch {
            print("ERROR: Getting log file size failed: \(error)")
            return false
        }
        if fileSize < 10000000 { // verifying that log file size is <10mb
            return true
        }
        
        removeLogFile()
        return true
    }
    
    
    /// Removes log file (with name kLogFileName) from Documents directory
    private class func removeLogFile() {
        if let dir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first {
            let fileLocation = dir.appendingPathComponent(kLogFileName)
            do {
                try FileManager.default.removeItem(at: fileLocation)
                print("INFO: Log file was removed")
            } catch {
                print("ERROR: Removing log file failed: \(error)")
            }
        }
    }

}
