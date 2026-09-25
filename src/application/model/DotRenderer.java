
package application.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javafx.scene.image.Image;
import net.sourceforge.plantuml.SourceStringReader;
import net.sourceforge.plantuml.FileFormat;
import net.sourceforge.plantuml.FileFormatOption;

/**
 * Helper class for rendering DOT graph code to an image
 */
public class DotRenderer {
    
    /**
     * Render DOT code to a JavaFX image
     * 
     * @param dotCode The DOT graph definition code
     * @return A JavaFX Image containing the rendered graph
     * @throws IOException If there's an error rendering the graph
     */
    public static Image renderDotToImage(String dotCode) throws IOException {
        // Remove any existing @start/@end tags to prevent issues
        dotCode = dotCode.replaceAll("@start\\w+", "").replaceAll("@end\\w+", "").trim();
        
        // PlantUML can directly render DOT code if wrapped properly
        String plantUmlCode = "@startdot\n" + dotCode + "\n@enddot";
        
        // Create a PlantUML reader
       SourceStringReader reader = new SourceStringReader(plantUmlCode);
        
        // Generate the image
        ByteArrayOutputStream os = new ByteArrayOutputStream();
       reader.outputImage(os, new FileFormatOption(FileFormat.PNG));
        os.close();
        
        // Convert to JavaFX Image and return
        return new Image(new ByteArrayInputStream(os.toByteArray()));
    }
    
    /**
     * Fix common errors in DOT code
     * 
     * @param dotCode The DOT code to fix
     * @return The fixed DOT code
     */
    public static String fixDotCode(String dotCode) {
        // Ensure it starts with digraph or graph if missing
        if (!dotCode.trim().startsWith("digraph") && !dotCode.trim().startsWith("graph")) {
            dotCode = "digraph G {\n" + dotCode;
            
            // And make sure it ends with a closing bracket if we added an opening one
            if (!dotCode.trim().endsWith("}")) {
                dotCode = dotCode.trim() + "\n}";
            }
        }
        
        return dotCode;
    }
}