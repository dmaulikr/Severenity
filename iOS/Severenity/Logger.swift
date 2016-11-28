//
//  Logger.swift
//  Severenity
//
//  Created by Yuriy Yasinskyy on 24.11.16.
//  Copyright © 2016 severenity. All rights reserved.
//

import Foundation

class Log: NSObject {
    
    class func info(message: String, sender: Any, detailed: Bool = false) {
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
        writeToFile(content: logContent)
    }
    
    class func error(message: String, sender: Any, detailed: Bool = false) {
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
        writeToFile(content: logContent)
    }
    
    // MARK: Working with file
    
    private class func writeToFile(content: String) {
        if let dir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first {
            let fileLocation = dir.appendingPathComponent(kLogFile)

            if FileManager.default.fileExists(atPath: fileLocation.path) && checkSizeOfLog(filePath: fileLocation.path) {
                do  {
                    let fileHandle = try FileHandle.init(forWritingTo: fileLocation)
                    fileHandle.seekToEndOfFile()
                    let dataToWrite = (content + "\n").data(using: String.Encoding.utf8)!
                    fileHandle.write(dataToWrite)
                    fileHandle.closeFile()
                } catch {
                    print("ERROR: Writing log to file failed: \(error)")
                }
            } else {
                FileManager.default.createFile(atPath: fileLocation.path, contents: content.data(using: String.Encoding.utf8)!, attributes: nil)
            }
        }
    }
    
    private class func readFromFile() -> String? {
        var content: String?
        if let dir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first {
            let path = dir.appendingPathComponent(kLogFile)
            do {
                content = try String(contentsOf: path, encoding: String.Encoding.utf8)
            } catch {
                print("ERROR: Reading log from file failed: \(error)")
            }
        }
        return content
    }
    
    private class func checkSizeOfLog(filePath: String) -> Bool {
        var fileSize : UInt64 = 0
        do {
            let fileAttributes = try FileManager.default.attributesOfItem(atPath: filePath)
            fileSize = fileAttributes[FileAttributeKey.size] as! UInt64
        } catch {
            print("ERROR: Getting log file size failed: \(error)")
            return false
        }
        
        if fileSize < 10000000 { //Verifying that log file size is <10mb
            return true
        } else {
            removeLogFile()
            return true
        }
    }
    
    private class func removeLogFile() {
        if let dir = FileManager.default.urls(for: .documentDirectory, in: .userDomainMask).first {
            let fileLocation = dir.appendingPathComponent(kLogFile)
            do {
                try FileManager.default.removeItem(at: fileLocation)
                print("INFO: Log file was removed")
            } catch {
                print("ERROR: Removing log file failed: \(error)")
            }
        }
    }

}
