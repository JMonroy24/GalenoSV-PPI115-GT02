sed -i '/<\/f:facet>/a \
            <f:facet name="relaciones">\
                <p:outputPanel rendered="#{consultaModel.estadoModificar}">\
                    <p:panel header="Procedimientos asignados" style="margin-top:1rem">\
                        <p:dataTable value="#{consultaModel.procedimientosAsignados}" var="proc" emptyMessage="Sin procedimientos." size="small">\
                            <p:column headerText="Procedimiento"><h:outputText value="#{proc.idProcedimiento.nombre}"/></p:column>\
                            <p:column headerText="Inicio"><h:outputText value="#{proc.fechaInicio}"><f:convertDateTime pattern="dd/MM/yyyy HH:mm"/></h:outputText></p:column>\
                            <p:column headerText="Fin"><h:outputText value="#{proc.fechaFin}"><f:convertDateTime pattern="dd/MM/yyyy HH:mm"/></h:outputText></p:column>\
                            <p:column headerText="Acción">\
                                <p:commandButton icon="pi pi-trash" styleClass="ui-button-danger" action="#{consultaModel.quitarProcedimiento(proc)}" process="@this" update="@form">\
                                    <p:confirm header="Confirmar" message="¿Remover procedimiento?" icon="pi pi-exclamation-triangle"/>\
                                </p:commandButton>\
                            </p:column>\
                        </p:dataTable>\
                        <p:panelGrid columns="2" style="width:100%; margin-top:1rem">\
                            <p:outputLabel value="Procedimiento:" for="idProcNew"/>\
                            <p:selectOneMenu id="idProcNew" value="#{consultaModel.nuevoProcedimiento.idProcedimiento}" converter="#{entityConverter}">\
                                <f:selectItem itemLabel="Seleccione..." itemValue="#{null}" noSelectionOption="true"/>\
                                <f:selectItems value="#{procedimientoModel.registros}" var="p" itemLabel="#{p.nombre}" itemValue="#{p}"/>\
                            </p:selectOneMenu>\
                            <p:outputLabel value="Inicio:" for="fInicioProcNew"/>\
                            <p:datePicker id="fInicioProcNew" value="#{consultaModel.nuevoProcedimiento.fechaInicio}" showTime="true" pattern="dd/MM/yyyy HH:mm"/>\
                            <p:outputLabel value="Fin:" for="fFinProcNew"/>\
                            <p:datePicker id="fFinProcNew" value="#{consultaModel.nuevoProcedimiento.fechaFin}" showTime="true" pattern="dd/MM/yyyy HH:mm"/>\
                            <p:outputLabel value="Observaciones:" for="obsProcNew"/>\
                            <p:inputTextarea id="obsProcNew" value="#{consultaModel.nuevoProcedimiento.observaciones}" rows="2"/>\
                            <h:outputText value=""/>\
                            <p:commandButton value="Agregar Procedimiento" icon="pi pi-plus" styleClass="ui-button-success" action="#{consultaModel.agregarProcedimiento()}" process="@this idProcNew fInicioProcNew fFinProcNew obsProcNew" update="@form"/>\
                        </p:panelGrid>\
                    </p:panel>\
                </p:outputPanel>\
            </f:facet>' src/main/webapp/paginas/clinica/consulta.xhtml
bash update_consulta.sh
