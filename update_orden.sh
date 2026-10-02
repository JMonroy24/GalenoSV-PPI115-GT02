sed -i '/<\/f:facet>/a \
            <f:facet name="relaciones">\
                <p:outputPanel rendered="#{ordenExamenModel.estadoModificar}">\
                    <p:panel header="Resultados" style="margin-top:1rem">\
                        <p:dataTable value="#{ordenExamenModel.resultadosAsignados}" var="res" emptyMessage="Sin resultados." size="small">\
                            <p:column headerText="Fecha"><h:outputText value="#{res.fechaCreacion}"><f:convertDateTime pattern="dd/MM/yyyy HH:mm"/></h:outputText></p:column>\
                            <p:column headerText="Resultado"><h:outputText value="#{res.resultado}"/></p:column>\
                            <p:column headerText="Interpretación"><h:outputText value="#{res.interpretacion}"/></p:column>\
                            <p:column headerText="Acción">\
                                <p:commandButton icon="pi pi-trash" styleClass="ui-button-danger" action="#{ordenExamenModel.quitarResultado(res)}" process="@this" update="@form">\
                                    <p:confirm header="Confirmar" message="¿Remover resultado?" icon="pi pi-exclamation-triangle"/>\
                                </p:commandButton>\
                            </p:column>\
                        </p:dataTable>\
                        <p:panelGrid columns="2" style="width:100%; margin-top:1rem">\
                            <p:outputLabel value="Resultado:" for="resNew"/>\
                            <p:inputTextarea id="resNew" value="#{ordenExamenModel.nuevoResultado.resultado}" rows="3"/>\
                            <p:outputLabel value="Interpretación:" for="intNew"/>\
                            <p:inputTextarea id="intNew" value="#{ordenExamenModel.nuevoResultado.interpretacion}" rows="3"/>\
                            <p:outputLabel value="Ruta del atestado:" for="rutaNew"/>\
                            <p:inputText id="rutaNew" value="#{ordenExamenModel.nuevoResultado.rutaAtestado}"/>\
                            <h:outputText value=""/>\
                            <p:commandButton value="Agregar Resultado" icon="pi pi-plus" styleClass="ui-button-success" action="#{ordenExamenModel.agregarResultado()}" process="@this resNew intNew rutaNew" update="@form"/>\
                        </p:panelGrid>\
                    </p:panel>\
                </p:outputPanel>\
            </f:facet>' src/main/webapp/paginas/clinica/ordenExamen.xhtml
bash update_orden.sh
