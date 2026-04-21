package giis.demo.tkrun.ResolverCostes;

import java.util.List;

import javax.swing.JOptionPane;

import giis.demo.util.ApplicationException;
import giis.demo.util.SwingUtil;

public class ResolverCostesController {
    private final ResolverCostesModel model;
    private final ResolverCostesView view;
    private final int incidenciaId;
    private final String tecnicoIdentificacion;
    private final Runnable onSuccess;
    private int step = 1; // 1=horas, 2=materiales

    public ResolverCostesController(ResolverCostesModel m, ResolverCostesView v, int incidenciaId, String tecnicoIdentificacion, Runnable onSuccess) {
        this.model = m;
        this.view = v;
        this.incidenciaId = incidenciaId;
        this.tecnicoIdentificacion = tecnicoIdentificacion;
        this.onSuccess = onSuccess;
        initController();
        view.getTxtId().setText(String.valueOf(incidenciaId));
        view.getFrame().setVisible(true);
    }

    private void initController() {
        view.getBtnAddMaterial().addActionListener(e -> SwingUtil.exceptionWrapper(() -> addMaterial()));
        view.getBtnRemoveMaterial().addActionListener(e -> SwingUtil.exceptionWrapper(() -> view.removeSelectedMaterial()));
        view.getBtnConfirmar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> confirmar()));
        view.getBtnCancelar().addActionListener(e -> SwingUtil.exceptionWrapper(() -> cancelar()));
        view.getBtnSiguiente().addActionListener(e -> SwingUtil.exceptionWrapper(() -> siguientePaso()));
        view.getBtnAtras().addActionListener(e -> SwingUtil.exceptionWrapper(() -> anteriorPaso()));
        // update total when fields change (simple approach: on focus loss)
        view.getTxtHoras().addActionListener(e -> view.updateTotal());
        view.getTxtCosteHora().addActionListener(e -> view.updateTotal());
    }

    private void siguientePaso() {
        if (step != 1) return;
        // validar horas y coste por hora antes de permitir materiales
        int horas = 0;
        double costeHora = 0.0;
        try { horas = Integer.parseInt(view.getTxtHoras().getText()); } catch (Exception ex) { throw new ApplicationException("Horas inválidas."); }
        try { costeHora = Double.parseDouble(view.getTxtCosteHora().getText()); } catch (Exception ex) { throw new ApplicationException("Coste por hora inválido."); }
        // pasar a paso 2 (materiales), conservar la información
        view.showStep(2);
        view.setMaterialsEnabled(true);
        step = 2;
        view.updateTotal();
    }

    private void anteriorPaso() {
        if (step != 2) return;
        // volver a paso 1 (horas), conservar la información
        view.showStep(1);
        view.setMaterialsEnabled(false);
        step = 1;
    }

    private void addMaterial() {
        String nombre = view.getTxtMatNombre().getText();
        String costeStr = view.getTxtMatCoste().getText();
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new ApplicationException("Nombre del material obligatorio.");
        }
        double coste = 0.0;
        try { coste = Double.parseDouble(costeStr); } catch (Exception ex) { throw new ApplicationException("Coste de material inválido."); }
        view.addMaterial(nombre.trim(), coste);
        view.getTxtMatNombre().setText("");
        view.getTxtMatCoste().setText("");
    }

    private void confirmar() {
        int horas = 0;
        double costeHora = 0.0;
        try { horas = Integer.parseInt(view.getTxtHoras().getText()); } catch (Exception ex) { throw new ApplicationException("Horas inválidas."); }
        try { costeHora = Double.parseDouble(view.getTxtCosteHora().getText()); } catch (Exception ex) { throw new ApplicationException("Coste por hora inválido."); }

        // obtener materiales y calcular totales para el resumen
        List<ResolverCostesModel.Material> materiales = view.getMateriales();
        double totalMateriales = 0.0;
        StringBuilder matList = new StringBuilder();
        if (materiales != null && !materiales.isEmpty()) {
            boolean first = true;
            for (ResolverCostesModel.Material m : materiales) {
                totalMateriales += m.coste;
                if (!first) matList.append("\n");
                matList.append(" - ").append(m.nombre).append(": ").append(m.coste);
                first = false;
            }
        }
        double totalHoras = horas * costeHora;
        double total = totalHoras + totalMateriales;

        // actualizar vista y mostrar resumen en popup de confirmación
        view.updateTotal();
        StringBuilder resumen = new StringBuilder();
        resumen.append("Resumen de resolución de incidencia ID ").append(incidenciaId).append("\n\n");
        resumen.append("Horas: ").append(horas).append("\n");
        resumen.append("Coste/hora: ").append(costeHora).append("\n");
        resumen.append("Total horas: ").append(totalHoras).append("\n\n");
        resumen.append("Materiales (total: ").append(totalMateriales).append("):\n");
        resumen.append(matList.length() == 0 ? " - (ninguno)" : matList.toString());
        resumen.append("\n\nCoste total: ").append(total).append("\n\n");
        resumen.append("¿Confirmar resolución y registro en historial?");

        int opt = JOptionPane.showConfirmDialog(view.getFrame(), resumen.toString(), "Confirmar resolución", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);
        if (opt != JOptionPane.YES_OPTION) return;

        model.resolverIncidencia(incidenciaId, tecnicoIdentificacion, horas, costeHora, materiales);
        view.getFrame().dispose();
        if (onSuccess != null) onSuccess.run();
    }

    private void cancelar() {
        view.getFrame().dispose();
    }
}
