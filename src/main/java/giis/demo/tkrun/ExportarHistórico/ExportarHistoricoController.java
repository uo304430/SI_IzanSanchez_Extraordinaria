package giis.demo.tkrun.ExportarHistórico;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import giis.demo.util.SwingUtil;

public class ExportarHistoricoController {
    private ExportarHistoricoModel model;
    private ExportarHistoricoView view;

    public ExportarHistoricoController(ExportarHistoricoModel m, ExportarHistoricoView v) {
        this.model = m;
        this.view = v;
        initView();
        initOptions();
        initController();
    }

    private void initView() {
        view.getFrame().setVisible(true);
    }

    private void initController() {
        view.getBtnChoose().addActionListener(e -> SwingUtil.exceptionWrapper(() -> choosePath()));
        view.getBtnExport().addActionListener(e -> SwingUtil.exceptionWrapper(() -> export()));
    }

    private void initOptions() {
        // populate tipo/zona combo boxes from DB
        try {
            Map<Integer,String> tipos = model.getTipoOptions();
            Map<Integer,String> zonas = model.getZonaOptions();
            view.setTipoOptions(tipos);
            view.setZonaOptions(zonas);
        } catch (Exception e) {
            // ignore errors populating combos, but log/show minimal info
            SwingUtil.showMessage("No se pudieron cargar las listas de tipos/zonas: " + e.getMessage(), "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void choosePath() {
        File f = view.showFileChooserWithDefault(model.defaultFileName());
        if (f != null) view.setPathText(f.getParent());
    }

    private void export() {
        String from = view.isDateChecked() ? view.getFromDate() : null;
        String to = view.isDateChecked() ? view.getToDate() : null;
        Integer tipo = view.isTipoChecked() ? view.getTipo() : null;
        Integer zona = view.isZonaChecked() ? view.getZona() : null;

        List<Map<String, Object>> incidencias = model.getIncidenciasWithHistorial(from, to, tipo, zona);

        String basePath = view.getPathText();
        if (basePath == null || basePath.isBlank()) basePath = System.getProperty("user.home");
        File f = new File(basePath, model.defaultFileName());
        // let user confirm/change filename
        File chosen = view.showFileChooserWithDefault(f.getName());
        if (chosen == null) {
            SwingUtil.showMessage("Exportación cancelada por el usuario.", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        model.writeIncidenciasAsJson(incidencias, chosen);
        SwingUtil.showMessage("Fichero generado: " + chosen.getAbsolutePath(), "Exportación completada", JOptionPane.INFORMATION_MESSAGE);
    }
}
