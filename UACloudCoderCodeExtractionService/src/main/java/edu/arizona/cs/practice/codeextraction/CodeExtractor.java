package edu.arizona.cs.practice.codeextraction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.cloudcoder.app.client.rpc.EditCodeService;
import org.cloudcoder.app.server.persist.Database;
import org.cloudcoder.app.server.persist.IDatabase;
import org.cloudcoder.app.server.persist.JDBCDatabaseConfig;
import org.cloudcoder.app.server.rpc.EditCodeServiceImpl;
import org.cloudcoder.app.shared.model.Change;
import org.cloudcoder.app.shared.model.ChangeType;
import org.cloudcoder.app.shared.model.Course;
import org.cloudcoder.app.shared.model.Event;
import org.cloudcoder.app.shared.model.EventType;
import org.cloudcoder.app.shared.model.Problem;
import org.cloudcoder.app.shared.model.ProblemList;
import org.cloudcoder.app.shared.model.ProblemText;
import org.cloudcoder.app.shared.model.User;

import edu.arizona.cs.practice.EDSUtil;

public class CodeExtractor {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        if (args.length < 3) {
            System.err.println("Usage: java CodeExtractor COURSE-ID COURSE-NUM SUFFIX OUTPUT-DIRECTORY PROBLEM-NAME1 ...");
            System.exit(1);
        }
        
        final String CLOUDCODER_ROOT = "/w/mse/cloudcoder";
        
        final int FIRST_PROBLEM_NAME_INDEX = 4;

        final int courseId = Integer.parseInt(args[0]);
        
        final String courseNum = args[1];
        
        final String SUFFIX = args[2];	// todo: use problem type
        
        final String output_directory = args[3];
        
        Properties dbconfig = new Properties();
        dbconfig.load(new FileInputStream(CLOUDCODER_ROOT + "/src/UACloudCoderCodeExtractionService/CodeExtractionService.properties"));
        JDBCDatabaseConfig.createFromProperties(dbconfig);

        IDatabase db = Database.getInstance();

        Course course = null;

        User whm = db.getUserWithoutAuthentication("whm");

        // No getCourse(), so get them all and find the course matching COURSE_ID.
        List<? extends Object[]> allCourses = db.getCoursesForUser(whm);
        for (Object[] o : allCourses) {
            System.out.println(o[0].getClass());
            Course c = (Course) o[0];
            System.out.format("%s: %d\n", c.getName(), c.getId());
            if (c.getId() == courseId)
                course = c;
        }
        System.out.format("Found course: %s\n", course);
        
        ArrayList<Integer> problemIds = new ArrayList<>();
findProblem:
        for (int i = FIRST_PROBLEM_NAME_INDEX; i < args.length; i++) {
            for (Problem problem: db.getProblemsInCourse(whm, course).getProblemList()) {
                //System.out.format("id: %d, testName: %s\n", problem.getProblemId(), problem.getTestname());
                if (problem.getTestname().equals(args[i])) {
                    System.out.format("%s is %d\n", args[i], problem.getProblemId());
                    problemIds.add(problem.getProblemId());
                    continue findProblem;
                }
            }
            System.err.println("Can't find '" + args[i] + "'");
            System.exit(1);
        }
        System.out.println("Problem ids:" + problemIds);
        
        Integer sections[] = db.getSectionsForCourse(course, whm);
        //Integer sections[] = { 207 };
        
        String root = String.format(CLOUDCODER_ROOT + "/extracted/%s/%s/", courseNum, output_directory);

        for (int sectionNum : sections) {
            System.out.format("---------- section = %d ----------\n",
                    sectionNum);
            List<User> students = db.getUsersInCourse(course.getId(),
                    sectionNum);

            for (User student : students) {
                /*
                String[] users = 
                    //{"li4","mgold1"};
                    { "mgold1" };
                if (!find(users, student.getUsername())) // FOR TESTING
                    continue;
                 */
                
                System.out.format("========== student = %s ===========\n", student.getUsername());
                String srcdir = root + String.format("%s/%s",
                        EDSUtil.decodeSection(sectionNum).substring(2),
                        student.getUsername());
                                
                for (int probnum : problemIds) {
                    Problem problem = Database.getInstance()
                            .getProblem(probnum);

                    String fname = problem.getTestname() + SUFFIX;
                    if (problem.getProblemId() == 610)      // Hack for duplicate problem name
                        fname = "concat_elements2.py";
                    
                    File dir = new File(srcdir);
                    dir.mkdirs();
                    
                    String fullpath = dir + "/" + fname;
                    System.out.format("extracting '%s'\n", fullpath);
                    LimitedEditCodeServiceImpl codeService = new LimitedEditCodeServiceImpl();
                    ProblemText problemText = codeService.doLoadCurrentText(student, problem);

                    
                    String code = problemText.getText() + "\n";
                    
                    String label = " (" + fullpath + ")";
                    
                    File pyfile = new File(fullpath);
                    try {
                        pyfile.createNewFile();
                    } catch (IOException e) {
                        System.out.format("ERROR: createNewFile failed on '%s' %s\n", pyfile, label);
                        e.printStackTrace();
                        //System.exit(1);
                    }
                    
                    FileWriter writer = null;
                    try {
                        writer = new FileWriter(pyfile);
                    } catch (IOException e) {
                        System.out.format("ERROR: new FileWriter(%s) failed %s\n", pyfile, label);  
                        e.printStackTrace();
                    }
                    try {
                        writer.write(code);
                    } catch (IOException e) {
                        System.out.format("ERROR: new writer.write(code) failed %s\n", pyfile, label);  
                        e.printStackTrace();
                    }
                    try {
                        writer.close();
                    } catch (IOException e) {
                        System.out.format("ERROR: new writer.close failed %s\n", pyfile, label);    
                        e.printStackTrace();
                    }

                }
            }
        }
        System.out.println("Done!");
    }

    private static boolean find(String[] strings, String s) {
        for (String entry: strings) {
            if (entry.equals(s))
                return true;
        }
        return false;
    }
}

// Change mostRecent = Database.getInstance().getMostRecentChange(user,
// 612);
