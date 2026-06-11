package giis.demo.tkrun.paqueteria.seguimiento;

import giis.demo.util.ApplicationException;
import giis.demo.util.SwingUtil;
import giis.demo.tkrun.paqueteria.login.SesionUsuario;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class SeguimientoController {

    private final SeguimientoListadoView view;
    private final SeguimientoModel model;
    // Cache de ids de la fila para abrir detalle (columna oculta logicamente)
    private List<EnvioListadoDto> enviosActuales;

    public SeguimientoController(SeguimientoListadoView view, SeguimientoModel model) {
        this.view  = view;
        this.model = model;
        initView();
        initListeners();
        cargarListado();
        view.setVisible(true);
    }

    private void initView() {
        SesionUsuario sesion = SesionUsuario.getInstance();
        view.getLblTitulo().setText("Mis envios  ·  " + sesion.getNombre());
    }

    private void initListeners() {
        view.getCmbEstado().addActionListener(e -> SwingUtil.exceptionWrapper(this::cargarListado));
        view.getBtnRefrescar().addActionListener(e -> SwingUtil.exceptionWrapper(this::cargarListado));
        view.getBtnBuscar().addActionListener(e -> SwingUtil.exceptionWrapper(this::buscarPorId));
        view.getBtnVerDetalle().addActionListener(e -> SwingUtil.exceptionWrapper(this::abrirDetalleSeleccionado));
        view.getBtnCerrar().addActionListener(e -> view.dispose());

        // Doble clic en la tabla
        view.getTblEnvios().addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2)
                    SwingUtil.exceptionWrapper(() -> abrirDetalleSeleccionado());
            }
        });
    }

    private void cargarListado() {
        SesionUsuario sesion = SesionUsuario.getInstance();
        int idx = view.getCmbEstado().getSelectedIndex();
        String filtro = (idx <= 0) ? null : SeguimientoListadoView.ESTADOS_FILTRO[idx];
        enviosActuales = model.getEnviosCliente(sesion.getIdUsuario(), filtro);
        rellenarTabla(enviosActuales);
    }

    private void rellenarTabla(List<EnvioListadoDto> envios) {
        view.getModeloTabla().setRowCount(0);
        for (EnvioListadoDto e : envios) {
            view.getModeloTabla().addRow(new Object[]{
                e.getCodigo(),
                e.getFechaRegistro(),
                e.getDestinatario(),
                e.getEstado(),
                e.getFechaEstimada()
            });
        }
    }

    private void buscarPorId() {
        String codigo = view.getTxtBuscarId().getText().trim();
        if (codigo.isEmpty())
            throw new ApplicationException("Introduzca el identificador del envio.");

        SesionUsuario sesion = SesionUsuario.getInstance();
        int idEnvio = model.buscarEnvioPorCodigo(codigo, sesion.getIdUsuario());

        if (idEnvio == -1) {
            JOptionPane.showMessageDialog(view,
                    "No se ha encontrado ningun envio con ese identificador asociado a su cuenta.",
                    "Envio no encontrado", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        abrirDetalle(idEnvio);
    }

    private void abrirDetalleSeleccionado() {
        int fila = view.getTblEnvios().getSelectedRow();
        if (fila < 0)
            throw new ApplicationException("Seleccione un envio de la tabla.");
        if (enviosActuales == null || fila >= enviosActuales.size()) return;
        abrirDetalle(enviosActuales.get(fila).getId());
    }

    private void abrirDetalle(int idEnvio) {
        SeguimientoDetalleView detalleView = new SeguimientoDetalleView(
                (JFrame) view.getOwner(), idEnvio);
        new SeguimientoDetalleController(detalleView, model, idEnvio);
    }
}
