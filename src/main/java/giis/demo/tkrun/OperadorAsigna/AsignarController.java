package giis.demo.tkrun.OperadorAsigna;

import java.util.List;
import java.util.ArrayList;

import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import giis.demo.tkrun.DTOs.IncidenciaDTO;
import giis.demo.util.ApplicationException;
import giis.demo.util.SwingUtil;

public class AsignarController {
    private AsignarModel model;
    private AsignarView view;
    private String operadorIdentificacion;

    public AsignarController(AsignarModel m, AsignarView v, String operadorIdentificacion) {
        this.model = m;
        this.view = v;
        this.operadorIdentificacion = operadorIdentificacion;
        initView();
        initController();
    }

    public void initView() {
        try {
            List<IncidenciaDTO> incidencias = model.getIncidenciasParaAsignar(operadorIdentificacion);
            view.populateIncidencias(incidencias);
            // inicialmente no mostrar técnicos hasta que se seleccione una incidencia
            view.populateTecnicosFromQuery(new java.util.ArrayList<>());
        } catch (Exception e) {
            throw new ApplicationException("Error inicializando incidencias para asignar: " + e.getMessage());
        }
        view.getFrame().setVisible(true);
    }

    public void initController() {
        view.getBtnAsignar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> asignarSeleccionada()));
        // when an incidencia is selected, filter técnicos by its tipo and show carga
        view.addIncidenciaSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                // avoid handling intermediate events
                if (e.getValueIsAdjusting()) return;
                SwingUtil.exceptionWrapper(() -> {
                    Integer tipo = view.getSelectedIncidenciaTipo();
                    System.out.println("Tipo de incidencia seleccionada: " + tipo);
                    if (tipo == null) {
                        view.populateTecnicosFromQuery(new ArrayList<>());
                        return;
                    }
                    List<Object[]> tecnicos = model.getTecnicosConCargaParaTipo(tipo);
                    if (tecnicos.isEmpty()) {
                        System.out.println("No hay técnicos asociados al tipo de incidencia " + tipo);
                        view.populateTecnicosFromQuery(new ArrayList<>());
                        return;
                    }
                    System.out.println("Ejemplo de técnico: " + tecnicos.get(0)[1] + ", Carga: " + tecnicos.get(0)[5]);
                    view.populateTecnicosFromQuery(tecnicos);
                });
            }
        });
    }

    private void asignarSeleccionada() {
        int idInc = view.getSelectedIncidenciaId();
        java.util.List<Integer> idsTec = view.getSelectedTecnicoIds();
        if (idInc == -1) {
            JOptionPane.showMessageDialog(view.getFrame(), "Seleccione una incidencia para asignar.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (idsTec == null || idsTec.isEmpty()) {
            JOptionPane.showMessageDialog(view.getFrame(), "Seleccione al menos un técnico para asignar.", "Sin selección", JOptionPane.WARNING_MESSAGE);
            return;
        }
        // obtain names directly from the model for the selected ids
        java.util.Map<Integer,String> nameById = model.getNombresTecnicosByIds(idsTec);
        StringBuilder listHtml = new StringBuilder("<html>Confirma que desea asignar la incidencia seleccionada a los siguientes técnicos:<br/>");
        for (int i=0;i<idsTec.size();i++) {
            Integer id = idsTec.get(i);
            String n = nameById.get(id);
            String display = (n == null) ? ("id=" + id) : (id + " - " + n);
            listHtml.append("&nbsp;&nbsp;- ").append(display).append("<br/>");
        }
        listHtml.append("</html>");
        int r = JOptionPane.showConfirmDialog(view.getFrame(), listHtml.toString(), "Confirmar asignación", JOptionPane.YES_NO_OPTION);
        if (r != JOptionPane.YES_OPTION) return;
        model.asignarIncidencia(idInc, idsTec, operadorIdentificacion);
        List<IncidenciaDTO> incidencias = model.getIncidenciasParaAsignar(operadorIdentificacion);
        view.populateIncidencias(incidencias);
        // show success message with assigned names
        StringBuilder names = new StringBuilder();
        for (int i=0;i<idsTec.size();i++) {
            Integer id = idsTec.get(i);
            String n = nameById.get(id);
            String display = (n == null) ? ("id=" + id) : (id + " - " + n);
            if (i>0) names.append(", ");
            names.append(display);
        }
        JOptionPane.showMessageDialog(view.getFrame(), "Incidencia asignada correctamente a: " + names.toString() + ". Registrado en el historial.", "Operación completada", JOptionPane.INFORMATION_MESSAGE);
    }
}
