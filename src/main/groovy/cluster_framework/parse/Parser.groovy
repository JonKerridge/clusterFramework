package cluster_framework.parse

import cluster_framework.records.ExtractVersion
import cluster_framework.records.ParseRecord
import cluster_framework.records.VersionControl
import groovyjarjarpicocli.CommandLine

/**
 * The class used to process a description of the processing required using
 * the cluster_framework_2 domain specific language.
 *
 * The class has a single method called parse()
 */
class Parser {

  String inputFileName, outputTextFile, outObjectFile
  String version = VersionControl.versionTag
/**
 * Create an instance of the parser
 * @param inFileName the full path name of the file containing the DSL specification,
 * excluding the suffix (.clic)
 */
  Parser(String inFileName) {
    this.inputFileName = inFileName + ".clic"
    outputTextFile = inputFileName + "txt"
    outObjectFile = inputFileName + "struct"
  }

  String hostIPAddress

  class VersionSpecification {
    @CommandLine.Parameters( description = "the software version number") String versionString
  } // VersionSpecification

  class EmitSpecification {
    @CommandLine.Option(names = ["-n", "-nodes"], description = "number of nodes") int nodes
    @CommandLine.Option(names = ["-w", "-workers"], description = "number of workers per nodeLoader") int workers
    @CommandLine.Option( names = "-p", split = "!") List<String> emitParamStrings
    @CommandLine.Option(names = ["-f", "-file"], split = ",", description ="")List <String> sourceDataFileNames
    // a comma separated string of types {! a comma separated string of parameter values}...
    // each string separated by ! and with NO spaces
    // the number of value parameter strings MUST match the value of nodes * workers
    @CommandLine.Parameters ( description =" IP address for each nodeLoader") List <String> nodeIPs
    // can be placed anywhere in specification but the number of specified IPs must match the number of nodes
  } // EmitSpecification

  class WorkSpecification {
    @CommandLine.Option(names = ["-n", "-nodes"], description = "number of nodes") int nodes
    @CommandLine.Option(names = ["-w", "-workers"], description = "number of workers per nodeLoader") int workers
    @CommandLine.Option(names = ["-m", "-method"], description = "name of work method used in this cluster") String workMethodName
    @CommandLine.Option( names = "-p", split = "!") List<String> workParamString
    // all workers have the same parameter values
    // added in version 2
    @CommandLine.Option(names = ["-f", "-file"], description = "full filename of shared data")String sharedDataFileName
    @CommandLine.Parameters ( description =" IP address for each nodeLoader") List <String> nodeIPs
    // can be placed anywhere in specification but the number of specified IPs must match the number of nodes
  } // WorkSpecification

  class CollectSpecification {
    @CommandLine.Option(names = ["-n", "-nodes"], description = "number of nodes") int nodes
    @CommandLine.Option(names = ["-w", "-workers"], description = "number of workers per nodeLoader") int workers
    @CommandLine.Option( names = ["-f", "-file"]) String outFileName  // base name of object file to which collected records are written
    @CommandLine.Option( names = "-cp", split = "!") List<String> collectParamString  // parameters of the collect method, one for all collect processes
    @CommandLine.Option( names = "-fp", split = "!") List<String> finaliseParamString  // parameters of the finalise method, one for all collect processes
    @CommandLine.Parameters ( description =" IP address for each nodeLoader") List <String> nodeIPs
    // can be placed anywhere in specification but the number of specified IPs must match the number of nodes
  } // CollectSpecification

  static boolean checkIPUniqueness (List<ParseRecord> buildData){
    List <String> usedIPs = []
    buildData.each {record ->
      if ( record.fixedIPAddresses != null)
        usedIPs = usedIPs + record.fixedIPAddresses
    }
    int totalSize = usedIPs.size()
    if (totalSize != usedIPs.toUnique().size()){
      println "The specified IPs are not unique $usedIPs"
      return false
    }
    else
      return true
  } // checkIPUniqueness

  List errorMessages = []

  boolean checkValidity (boolean test, String message){
    if (test)
      return true
    else {
      println "$message"
      errorMessages << message
      return false
    }
  }
/**
 * Used to invoke the parsing of the input file
 *
 * @return indicates whether the parsing was successful or not
 */
  boolean parse(){
    int emitterProcesses, workClusters
    workClusters = 0
    boolean outcome
    outcome = true
    if (!ExtractVersion.extractVersion(version, "Local")){
      println "cluster_framework_2: Version $version needs to downloaded, please modify the gradle.build file"
      System.exit(-1)
    }
    List<ParseRecord> buildData
    buildData = []
    new File(inputFileName).eachLine{ String inLine ->
      List<String> tokens = inLine.tokenize()
      String lineType = tokens.pop()
      String[] args = tokens.toArray(new String[0])
      ParseRecord parseRecord = new ParseRecord()
      switch (lineType) {
        case 'version':
          VersionSpecification versionSpecification = new VersionSpecification()
          new CommandLine(versionSpecification).parseArgs(args)
          println "Version = ${versionSpecification.versionString}"
          parseRecord.typeName = lineType
          parseRecord.version = versionSpecification.versionString
          assert version == parseRecord.version:"Version mismatch - Software is $version, specification expecting ${versionSpecification.versionString}"
          buildData << parseRecord
          break

        case 'emit':
          EmitSpecification emit = new EmitSpecification()
          new CommandLine(emit).parseArgs(args)
          int totalParamStrings = emit.nodes * emit.workers
          emitterProcesses = totalParamStrings
//          println "Emit: Nodes = ${emit.nodes}, Workers = ${emit.workers}, IPs = ${emit.nodeIPs}, Params = ${emit.emitParamStrings}, size expected ${totalParamStrings + 1} size supplied ${emit.emitParamStrings.size()}"
          if (emit.emitParamStrings != null) {
            if (!checkValidity((emit.emitParamStrings.size() == (totalParamStrings + 1)),
                "Emit must have ${(totalParamStrings + 1)} parameter strings; ${emit.emitParamStrings.size()} supplied ")) outcome = false
          }
          if (emit.nodeIPs != null)
            if (!checkValidity( (emit.nodes == emit.nodeIPs.size()),
                "Emit: Number of specified IPs must be same as number of nodes") )
              outcome = false
          parseRecord.typeName = lineType
          parseRecord.hostAddress = hostIPAddress
          parseRecord.nodes = emit.nodes
          parseRecord.workers = emit.workers
          parseRecord.version = version
          if (emit.nodeIPs != null)
            emit.nodeIPs.each { parseRecord.fixedIPAddresses << it }
          // deal with the mandatory parameter string associated with each emitter
          emit.emitParamStrings.each { String paramSpec ->
            List<String> tokenizedParams
            tokenizedParams = paramSpec.tokenize(',')
            parseRecord.emitParameters << tokenizedParams
          }
          if (emit.sourceDataFileNames != null)
            if (!checkValidity( (emit.sourceDataFileNames.size() == emitterProcesses),
              "source must have ${emitterProcesses} file names specified; ${emit.sourceDataFileNames.size()} supplied"))
              outcome = false
          parseRecord.sourceDataFileNames = emit.sourceDataFileNames
          println "$parseRecord"
          buildData << parseRecord
          break

        case 'work':
          WorkSpecification work = new WorkSpecification()
          new CommandLine(work).parseArgs(args)
//          println "Work: Nodes = ${work.nodes}, Workers = ${work.workers}, Method = ${work.workMethodName}, IPs = ${work.nodeIPs}, Params = ${work.workParamString}"
          if (work.nodeIPs != null)
            if (!checkValidity( (work.nodes == work.nodeIPs.size()),
                "Work: Number of specified IPs must be same as number of nodes") )
              outcome = false
          parseRecord.typeName = lineType
          parseRecord.hostAddress = hostIPAddress
          parseRecord.nodes = work.nodes
          parseRecord.workers = work.workers
          parseRecord.version = version
          parseRecord.workDataFileName = work.sharedDataFileName  // could be null
          if (work.nodeIPs != null)
            work.nodeIPs.each { parseRecord.fixedIPAddresses << it }
          parseRecord.workMethodName = work.workMethodName
          if (work.workParamString != null){
            if (!checkValidity((work.workParamString.size() == 2),
                "Work: The parameter string must consist of ONE type list followed by ONE value list"))
              outcome = false
            work.workParamString.each { String paramSpec ->
              List<String> tokenizedParams
              tokenizedParams = paramSpec.tokenize(',')
              parseRecord.workParameters << tokenizedParams
            }
          }
          println "$parseRecord"
          buildData << parseRecord
          break

        case 'collect':
          CollectSpecification collect = new CollectSpecification()
          new CommandLine(collect).parseArgs(args)
//          println "Collect: Nodes = ${collect.nodes}, Workers = ${collect.workers}, OutFile = ${collect.outFileName}, " +
//              "Collect Params = ${collect.collectParamStrings}, " +
//              "Finalise Params = ${collect.finaliseParamStrings}, IPs = ${collect.nodeIPs}"
          if (collect.nodeIPs != null)
            if (!checkValidity( (collect.nodes == collect.nodeIPs.size()),
                "Collect: Number of specified IPs must be same as number of nodes") )
              outcome = false
          int totalParamString = collect.nodes * collect.workers
          if (collect.collectParamString != null)
            if (!checkValidity((collect.collectParamString.size() == totalParamString +1),
                "Collect: The collate method must have ${(totalParamString+1)} parameter strings; ${collect.collectParamString.size()} supplied"))
              outcome = false
          if (collect.finaliseParamString != null)
            if (!checkValidity((collect.finaliseParamString.size() == totalParamString +1),
                "Collect: The finalise method must have ${(totalParamString+1)} parameter strings; ${collect.finaliseParamString.size()} supplied"))
              outcome = false
          parseRecord.typeName = lineType
          parseRecord.hostAddress = hostIPAddress
          parseRecord.nodes = collect.nodes
          parseRecord.workers = collect.workers
          parseRecord.version = version
          parseRecord.outFileName = collect.outFileName // could be null
          if (collect.nodeIPs != null)
            collect.nodeIPs.each { parseRecord.fixedIPAddresses << it }
          if (collect.collectParamString != null)
            collect.collectParamString.each { String paramSpec ->
              List<String> tokenizedParams
              tokenizedParams = paramSpec.tokenize(',')
              parseRecord.collectParameters << tokenizedParams
            }
          if (collect.finaliseParamString != null)
            collect.finaliseParamString.each { String paramSpec ->
              List<String> tokenizedParams
              tokenizedParams = paramSpec.tokenize(',')
              parseRecord.finaliseParameters << tokenizedParams
            }
          println "$parseRecord"
          buildData << parseRecord
          break
        default:
          println "$lineType incorrectly specified"
          outcome = false
          break
      }
    }  // file each line
    // check all specified IP addresses for nodes are unique
    if (checkIPUniqueness(buildData)) {
      File outFile = new File(outputTextFile)
      PrintWriter printWriter = outFile.newPrintWriter()
      buildData.each { printWriter.println "$it" }
      if (!outcome)
        errorMessages.each {printWriter.println "$it" }
      printWriter.flush()
      printWriter.close()
      File outObjFile = new File(outObjectFile)
      ObjectOutputStream outStream = outObjFile.newObjectOutputStream()
      buildData.each { outStream << it }
      outStream.flush()
      outStream.close()
    } else {
      println "The specified IP addresses are not unique - no output files produced"
      outcome = false
    }
    if (outcome)
      println "Parsing completed - no errors in $inputFileName"
    else
      println "Parsing failed, see errors highlighted above in $inputFileName"
    return outcome
  }// parse
}
