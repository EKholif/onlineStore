package frontEnd.utilites;

import org.springframework.web.servlet.ModelAndView;

public class SaveAndUpdate {


    private String listItems;



    public ModelAndView UpdateForm(ModelAndView model, String objectName, Object object, Integer id) {
        model.addObject(objectName, object);
        model.addObject("id", id);
        model.addObject("listItems", listItems); // Assuming listItems is a class variable
        model.addObject("pageTitle", " Edit : " + objectName + " ID :  " + id);

        model.addObject("saveChanges", "/" + objectName + "/save-edit-" + objectName + "/");
        return model;
    }







}
