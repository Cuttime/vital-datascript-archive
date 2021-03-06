/*   CONFIG PARAMS   */

def SERVICE_PROFILE = 'primelocal'
def SERVICE_KEY     = 'serv-serv-serv'

def VIDEO_ID = args[0]
def OUTPUT_FILE = args[1]

def YOUTUBE_API_KEY = 'key'

def TEXT_FORMAT = 'plainText' // 'html'

def MAX = 10

def MIN_DATE = null// java.util.Date

def MAX_DATE = null// java.util.Date

def PARTS = 'snippet,replies' // 'snippet', 'replies'


/*   END OF CONFIG   */


import ai.vital.vitalsigns.block.BlockCompactStringSerializer;
import ai.vital.vitalsigns.model.VitalServiceKey
import ai.vital.vitalservice.factory.VitalServiceFactory
import com.vitalai.domain.social.YouTubeComment;


println "Video ID:: ${VIDEO_ID}"


boolean pretty = ( OUTPUT_FILE == '-print' || OUTPUT_FILE == '--print') 

File outputFile = null

if(!pretty) {
	
	outputFile = new File(OUTPUT_FILE)
	
	println "Output file: ${outputFile.absolutePath}"
	
	if(outputFile.exists()) {
		System.err.println("Output file already exists: ${outputFile.absolutePath}")
		return
	}
	
	
} else {

	println "Printing results to the console"	

}

//necessary, as groovy shell does not 
ai.vital.vitalsigns.VitalSigns.get().registerOntology(new com.vitalai.domain.nlp.ontology.Ontology(), 'vital-nlp-groovy-0.2.300.jar')
ai.vital.vitalsigns.VitalSigns.get().registerOntology(new com.vitalai.domain.social.ontology.Ontology(), 'vital-social-groovy-0.2.300.jar')


def vitalService = VitalServiceFactory.openService(new VitalServiceKey(key: SERVICE_KEY), SERVICE_PROFILE)

def scriptParams = [
	//type is constant
	key: YOUTUBE_API_KEY,
	textFormat: TEXT_FORMAT,
	videoId: VIDEO_ID,
	max: MAX,
	minDate: MIN_DATE,
	maxDate: MAX_DATE,
	parts: PARTS
]

def resultList = vitalService.callFunction('commons/scripts/YouTubeCommentsScript.groovy', scriptParams)

println "STATUS: ${resultList.status}"

BlockCompactStringSerializer writer = outputFile != null ?  new BlockCompactStringSerializer(outputFile) : null

int i = 0

for(YouTubeComment comment : resultList) {
	
	i++
	
	if(writer != null) {
		
		writer.startBlock()
		
		writer.writeGraphObject(comment)
		
		writer.endBlock()
		
	} else {
	
		String commentID = comment.commentID
		
		String videoID = comment.videoID
		
		String channelID = comment.channelID
		
		String text = comment.body
		
		String authorName = comment.authorName
		
		Date pubDate = comment.publicationDate.getDate()
		
		Integer likeCount = comment.likeCount.intValue()
		
		println "${i}. ${pubDate} ${authorName}: ${text}  likes: ${likeCount} [${commentID}]"
	
	}
	
	
	
	
}


if(writer != null) {
	writer.close()
}

println "YT comments iterated: ${i}"

