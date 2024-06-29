package dmit2015.faces;

import dmit2015.entity.WeatherData;
import dmit2015.repository.WeatherDataRepository;
import jakarta.annotation.PostConstruct;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;
import org.primefaces.PrimeFaces;

import java.io.Serializable;
import java.util.List;

@Named("currentWeatherDataCrudView")
@ViewScoped
public class WeatherDataCrudView implements Serializable {

    @Inject
    private WeatherDataRepository _weatherDataRepository;

    @Getter
    private List<WeatherData> weatherDatas;

    @Getter
    @Setter
    private WeatherData selectedWeatherData;

    @Getter
    @Setter
    private Long selectedId;

    @PostConstruct  // After @Inject is complete
    public void init() {
        try {
            weatherDatas = _weatherDataRepository.findAll();
        } catch (Exception ex) {
            handleException(ex);
        }
    }

    public void onOpenNew() {
        selectedWeatherData = new WeatherData();
    }

    public void onSave() {
        if (selectedId == null) {
            try {
                _weatherDataRepository.add(selectedWeatherData);
                Messages.addGlobalInfo("Create was successful. {0}", selectedWeatherData.getId());
                weatherDatas = _weatherDataRepository.findAll();
            } catch (RuntimeException ex) {
                Messages.addGlobalError(ex.getMessage());
            } catch (Exception ex) {
                Messages.addGlobalError("Create was not successful. {0}", ex.getMessage());
                handleException(ex);
            }
        } else {
            try {
                _weatherDataRepository.update(selectedId, selectedWeatherData);
                Messages.addFlashGlobalInfo("Update was successful.");
                weatherDatas = _weatherDataRepository.findAll();
            } catch (RuntimeException ex) {
                Messages.addGlobalError(ex.getMessage());
            } catch (Exception ex) {
                Messages.addGlobalError("Update was not successful.");
                handleException(ex);
            }
        }

        PrimeFaces.current().executeScript("PF('manageWeatherDataDialog').hide()");
        PrimeFaces.current().ajax().update("form:messages", "form:dt-WeatherDatas");
    }

    public void onDelete() {
        try {
            _weatherDataRepository.delete(selectedWeatherData);
            selectedWeatherData = null;
            Messages.addGlobalInfo("Delete was successful.");
            weatherDatas = _weatherDataRepository.findAll();
            PrimeFaces.current().ajax().update("form:messages", "form:dt-WeatherDatas");
        } catch (RuntimeException ex) {
            Messages.addGlobalError(ex.getMessage());
        } catch (Exception ex) {
            Messages.addGlobalError("Delete not successful.");
            handleException(ex);
        }
    }

    /**
     * This method is used to handle exceptions and display root cause to user.
     *
     * @param ex The Exception to handle.
     */
    protected void handleException(Exception ex) {
        StringBuilder details = new StringBuilder();
        Throwable causes = ex;
        while (causes.getCause() != null) {
            details.append(ex.getMessage());
            details.append("    Caused by:");
            details.append(causes.getCause().getMessage());
            causes = causes.getCause();
        }
        Messages.create(ex.getMessage()).detail(details.toString()).error().add("errors");
    }

}