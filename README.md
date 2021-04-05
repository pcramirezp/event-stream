# Spring Boot Reactive File Service

El siguiente script del programa muestra el REST Controller ReactiveFileService con el que cargamos un archivo local basado en el parámetro Path URL y lo transferimos reactivo al cliente HTTP usando Flux <DataBuffer>.

```java
    @RestController
    @RequestMapping(value = "/rest/file")
    public class ReactiveFileService {
       public static final int defaultBufferSize = 1 << 12;
       public ReactiveFileService() {
          super();
       }
       @GetMapping
       public Flux<DataBuffer> get(ServerWebExchange webExchange) throws Exception {
          List pathList = webExchange.getRequest().getQueryParams().get("path");
          ServerHttpResponse serverHttpResponse = webExchange.getResponse();
          DataBufferFactory dataBufferFactory = webExchange.getResponse().bufferFactory();
          if (pathList == null) {
             serverHttpResponse.getHeaders().add("Content-Type", "text/html; charset=UTF-8");
             DataBuffer replyDataBuffer = dataBufferFactory.allocateBuffer(defaultBufferSize)
                      .write("path is null".getBytes(StandardCharsets.UTF_8));
             return Flux.just(replyDataBuffer);
          }
          String path = pathList.get(0);
          String mimeType = Files.probeContentType(Paths.get(path));
          if (mimeType == null) {
             MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
             mimeType = mimeTypesMap.getContentType(path);
          }
          serverHttpResponse.getHeaders().add("Content-Type", mimeType);
          Flux result = DataBufferUtils
             .readByteChannel(() -> FileChannel.open(Paths.get(path), StandardOpenOption.READ), dataBufferFactory, defaultBufferSize)
             .onErrorResume(ex -> {
                serverHttpResponse.getHeaders().add("Content-Type", "text/html; charset=UTF-8");
                DataBuffer replyDataBuffer = dataBufferFactory.allocateBuffer(defaultBufferSize).write(
                   ("path " + path + " not found, ex = " + ex.getMessage()).getBytes(StandardCharsets.UTF_8));
                return Flux.just(replyDataBuffer);
             });
             return result;
        }
    }

```

La siguiente lista muestra la aplicación Spring Boot:

```java
@SpringBootApplication
public class ReactiveFileServiceApplication {
   public static void main(String[] args) {
      SpringApplication.run(ReactiveFileServiceApplication.class, args);
   }
}

```

Después de iniciar la aplicación Spring Boot, el servicio de archivos reactivos está activo y se puede abordar según la configuración de la siguiente manera:

http://localhost:8080/rest/file?ruta = /mypath/myfile.txt

Si el archivo existe, se transmite de forma reactiva al cliente.

La siguiente lista muestra el cliente de servicio de archivos reactivos Spring Boot correspondiente desarrollado como una aplicación de línea de comandos:

```java
@SpringBootApplication
public class ReactiveFileServiceClient implements ApplicationRunner {
   public static void main(String[] args) throws Exception {
      SpringApplication app = new SpringApplication(ReactiveFileServiceClient.class);
      app.setWebApplicationType(WebApplicationType.NONE);
       app.run(args);
   }
   @Override
   public void run(ApplicationArguments args) throws Exception {
       URL url = null;
       try {
           url = new URL(args.getOptionValues("url").get(0));
       } catch (Exception e) {
           System.err.println("missing --url option argument");
           this.help();
           return;
       }
       String out = null;
       try {
           out = args.getOptionValues("out").get(0);
       } catch (Exception e) {
      }
      String surl = url.getProtocol() + "://" + url.getHost() + ":" + url.getPort();
      String path = url.getFile();
      Flux<DataBuffer> data = WebClient.create(surl).get().uri(path).retrieve().bodyToFlux(DataBuffer.class);
      if (out != null) {
         try (FileOutputStream fos = new FileOutputStream(out)) {
           DataBufferUtils.write(data, fos).map(DataBufferUtils::release).blockLast();
         }
         System.out.println("result written to file " + out);
      }
    }
    private void help() {
       System.out.println("usage java -jar reactivefileclient-0.0.1-SNAPSHOT.jar  --url= --out=");
       System.out.println("example:");
       System.out.println("java -jar reactivefileclient-0.0.1-SNAPSHOT.jar --url=http://localhost:8080/rest/file?path=in/bigimage.jpg --out=out/bigimage.jpg");
   }
}
```

El archivo bigimage.jpg se puede reemplazar por un archivo real existente. La ruta de salida se puede ajustar en consecuencia.